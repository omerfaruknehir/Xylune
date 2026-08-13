#!/usr/bin/env bash
set -uo pipefail

ROOT="${GITHUB_WORKSPACE:-$(pwd)}"
OUT="$ROOT/qa-artifacts"
PACKAGE="app.xylune.chat.debug"
ACTIVITY="app.xylune.chat.MainActivity"
mkdir -p "$OUT"
status=0

record_failure() {
  echo "$1" | tee -a "$OUT/qa-summary.txt" >&2
  status=1
}

capture_screen() {
  local name="$1"
  adb exec-out uiautomator dump /dev/tty > "$OUT/${name}-ui.xml" 2> "$OUT/${name}-ui-error.txt" || true
  adb exec-out screencap -p > "$OUT/${name}.png" 2> "$OUT/${name}-screenshot-error.txt" || true
}

pick_node_center() {
  local file="$1"
  local attr="$2"
  local target="$3"
  python3 - "$file" "$attr" "$target" <<'PY'
import html
import re
import sys

path, attr, target = sys.argv[1], sys.argv[2], sys.argv[3]
data = open(path, encoding="utf-8", errors="replace").read()
for tag in re.findall(r"<node\b[^>]*>", data):
    attrs = dict(re.findall(r'([\w-]+)="([^"]*)"', tag))
    if html.unescape(attrs.get(attr, "")) != target:
        continue
    m = re.fullmatch(r"\[(\d+),(\d+)\]\[(\d+),(\d+)\]", attrs.get("bounds", ""))
    if m:
        x1, y1, x2, y2 = map(int, m.groups())
        print((x1 + x2) // 2, (y1 + y2) // 2)
        raise SystemExit(0)
raise SystemExit(1)
PY
}

pick_text_center() {
  pick_node_center "$1" text "$2"
}

pick_desc_center() {
  pick_node_center "$1" content-desc "$2"
}

tap_text() {
  local file="$1"
  local text="$2"
  local label="$3"
  local xy
  if xy="$(pick_text_center "$file" "$text")"; then
    read -r x y <<<"$xy"
    adb shell input tap "$x" "$y"
    echo "${label}=PASS text=${text} coord=${x},${y}" >> "$OUT/qa-summary.txt"
    return 0
  fi
  record_failure "${label}=FAIL node-not-found text=$text"
  return 1
}

tap_desc() {
  local file="$1"
  local desc="$2"
  local label="$3"
  local xy
  if xy="$(pick_desc_center "$file" "$desc")"; then
    read -r x y <<<"$xy"
    adb shell input tap "$x" "$y"
    echo "${label}=PASS desc=${desc} coord=${x},${y}" >> "$OUT/qa-summary.txt"
    return 0
  fi
  record_failure "${label}=FAIL node-not-found desc=$desc"
  return 1
}

tap_provider_cta() {
  local file="$1"
  local text xy
  for text in "Set up a provider" "Set up provider" "Add provider"; do
    if xy="$(pick_text_center "$file" "$text")"; then
      read -r x y <<<"$xy"
      adb shell input tap "$x" "$y"
      echo "tapProviderCta=PASS text=${text} coord=${x},${y}" >> "$OUT/qa-summary.txt"
      return 0
    fi
  done
  record_failure "tapProviderCta=FAIL no-provider-cta-node"
  return 1
}

dismiss_quickstep_anr() {
  local name="$1"
  local i
  for i in 1 2 3; do
    if ! grep -Fq "Quickstep isn't responding" "$OUT/${name}-ui.xml" 2>/dev/null; then
      return 0
    fi
    echo "quickstepAnrObserved=INFO attempt=$i" >> "$OUT/qa-summary.txt"
    local xy
    if xy="$(pick_text_center "$OUT/${name}-ui.xml" "Wait")"; then
      read -r x y <<<"$xy"
      adb shell input tap "$x" "$y"
      sleep 2
      capture_screen "$name"
    else
      return 1
    fi
  done
  ! grep -Fq "Quickstep isn't responding" "$OUT/${name}-ui.xml" 2>/dev/null
}

dismiss_release_dialog() {
  local name="$1"
  if ! grep -Fq 'Open release' "$OUT/${name}-ui.xml" 2>/dev/null; then
    return 0
  fi
  local xy
  if xy="$(pick_text_center "$OUT/${name}-ui.xml" "Later")"; then
    read -r x y <<<"$xy"
    adb shell input tap "$x" "$y"
    echo "releaseDialogDismissed=PASS coord=${x},${y}" >> "$OUT/qa-summary.txt"
    sleep 2
    capture_screen "$name"
    dismiss_quickstep_anr "$name"
    return $?
  fi
  record_failure "releaseDialogDismissed=FAIL Later-node-not-found"
  return 1
}

{
  echo "Xylune Android emulator QA"
  echo "commit=${GITHUB_SHA:-unknown}"
  echo "utc=$(date -u +%Y-%m-%dT%H:%M:%SZ)"
} > "$OUT/qa-summary.txt"

adb wait-for-device
adb shell getprop > "$OUT/getprop.txt" 2>&1 || true
adb devices -l > "$OUT/adb-devices.txt" 2>&1 || true

if ./gradlew --no-daemon --stacktrace --max-workers=2 connectedDebugAndroidTest \
    > "$OUT/connectedDebugAndroidTest.txt" 2>&1; then
  echo "instrumentation=PASS" >> "$OUT/qa-summary.txt"
else
  echo "instrumentation=FAIL" >> "$OUT/qa-summary.txt"
  status=1
fi

if ./gradlew --no-daemon --stacktrace --max-workers=2 :app:installDebug \
    > "$OUT/installDebug.txt" 2>&1; then
  echo "installDebug=PASS" >> "$OUT/qa-summary.txt"
else
  record_failure "installDebug=FAIL"
fi

adb shell am force-stop "$PACKAGE" >/dev/null 2>&1 || true
adb shell pm clear "$PACKAGE" > "$OUT/pm-clear.txt" 2>&1 || true
adb logcat -c || true

if adb shell am start -W -n "$PACKAGE/$ACTIVITY" > "$OUT/launch.txt" 2>&1; then
  echo "launchCommand=PASS" >> "$OUT/qa-summary.txt"
else
  record_failure "launchCommand=FAIL"
fi

sleep 5
capture_screen welcome
if dismiss_quickstep_anr welcome; then
  echo "systemOverlayClear=PASS" >> "$OUT/qa-summary.txt"
else
  record_failure "systemOverlayClear=FAIL Quickstep ANR persisted"
fi

if grep -q '<hierarchy' "$OUT/welcome-ui.xml" 2>/dev/null; then
  echo "welcomeUiHierarchy=PASS" >> "$OUT/qa-summary.txt"
else
  record_failure "welcomeUiHierarchy=FAIL"
fi
[[ -s "$OUT/welcome.png" ]] && echo "welcomeScreenshot=PASS" >> "$OUT/qa-summary.txt" \
  || record_failure "welcomeScreenshot=FAIL"

if tap_text "$OUT/welcome-ui.xml" "Skip for now" "tapSkipForNow"; then
  sleep 4
  capture_screen main
  dismiss_quickstep_anr main || record_failure "mainSystemOverlayClear=FAIL"
  dismiss_release_dialog main || true

  if grep -q '<hierarchy' "$OUT/main-ui.xml" 2>/dev/null; then
    echo "mainUiHierarchy=PASS" >> "$OUT/qa-summary.txt"
  else
    record_failure "mainUiHierarchy=FAIL"
  fi
  [[ -s "$OUT/main.png" ]] && echo "mainScreenshot=PASS" >> "$OUT/qa-summary.txt" \
    || record_failure "mainScreenshot=FAIL"

  if tap_provider_cta "$OUT/main-ui.xml"; then
    sleep 3
    capture_screen provider
    dismiss_quickstep_anr provider || record_failure "providerSystemOverlayClear=FAIL"
    if grep -q '<hierarchy' "$OUT/provider-ui.xml" 2>/dev/null; then
      echo "providerUiHierarchy=PASS" >> "$OUT/qa-summary.txt"
    else
      record_failure "providerUiHierarchy=FAIL"
    fi
    [[ -s "$OUT/provider.png" ]] && echo "providerScreenshot=PASS" >> "$OUT/qa-summary.txt" \
      || record_failure "providerScreenshot=FAIL"

    # Provider Back returns to Settings home. Scroll using bounds from the actual scrollable UI node,
    # then open Search & web and capture the redesigned settings screen.
    adb shell input keyevent 4
    sleep 2
    capture_screen settings-home
    dismiss_quickstep_anr settings-home || record_failure "settingsHomeSystemOverlayClear=FAIL"
    settings_ui="$OUT/settings-home-ui.xml"
    for scroll_attempt in 1 2 3; do
      if pick_text_center "$settings_ui" "Search & web" >/dev/null 2>&1; then
        break
      fi
      scroll_xy="$(python3 - "$settings_ui" <<'PY2'
import re, sys
data = open(sys.argv[1], encoding='utf-8', errors='replace').read()
for tag in re.findall(r'<node\b[^>]*>', data):
    if 'scrollable="true"' not in tag:
        continue
    m = re.search(r'bounds="\[(\d+),(\d+)\]\[(\d+),(\d+)\]"', tag)
    if not m:
        continue
    x1, y1, x2, y2 = map(int, m.groups())
    x = (x1 + x2) // 2
    print(x, y1 + (y2-y1)*3//4, x, y1 + (y2-y1)//4)
    raise SystemExit(0)
raise SystemExit(1)
PY2
      )" || true
      if [[ -z "$scroll_xy" ]]; then
        record_failure "scrollSettingsHome=FAIL scrollable-node-not-found attempt=$scroll_attempt"
        break
      fi
      read -r sx sy ex ey <<<"$scroll_xy"
      adb shell input swipe "$sx" "$sy" "$ex" "$ey" 350
      echo "scrollSettingsHome=PASS attempt=$scroll_attempt coord=${sx},${sy}->${ex},${ey}" >> "$OUT/qa-summary.txt"
      sleep 2
      capture_name="settings-home-scroll-${scroll_attempt}"
      capture_screen "$capture_name"
      dismiss_quickstep_anr "$capture_name" || record_failure "settingsScrollSystemOverlayClear=FAIL attempt=$scroll_attempt"
      settings_ui="$OUT/${capture_name}-ui.xml"
    done
    if tap_text "$settings_ui" "Search & web" "openSearchSettings"; then
      sleep 3
      capture_screen search
      if grep -Fq 'Search routing' "$OUT/search-ui.xml" 2>/dev/null && grep -Fq 'Automatic' "$OUT/search-ui.xml" 2>/dev/null; then
        echo "searchSettingsUi=PASS" >> "$OUT/qa-summary.txt"
      else
        record_failure "searchSettingsUi=FAIL expected-compact-search-controls-missing"
      fi
      [[ -s "$OUT/search.png" ]] && echo "searchScreenshot=PASS" >> "$OUT/qa-summary.txt" \
        || record_failure "searchScreenshot=FAIL"
    fi
  fi
fi

adb shell dumpsys activity top > "$OUT/activity-top.txt" 2>&1 || true
adb shell dumpsys window windows > "$OUT/windows.txt" 2>&1 || true
adb shell dumpsys package "$PACKAGE" > "$OUT/package.txt" 2>&1 || true
adb shell dumpsys meminfo "$PACKAGE" > "$OUT/meminfo.txt" 2>&1 || true
adb shell dumpsys gfxinfo "$PACKAGE" > "$OUT/gfxinfo.txt" 2>&1 || true
adb shell pidof -s "$PACKAGE" > "$OUT/pid.txt" 2>&1 || true
adb logcat -b crash -d > "$OUT/logcat-crash.txt" 2>&1 || true
adb logcat -d > "$OUT/logcat.txt" 2>&1 || true

pid="$(tr -d '[:space:]' < "$OUT/pid.txt" 2>/dev/null || true)"
if [[ -n "$pid" ]]; then
  echo "processAlive=PASS pid=$pid" >> "$OUT/qa-summary.txt"
else
  record_failure "processAlive=FAIL"
fi

if grep -Eiq 'FATAL EXCEPTION|Process: app\.xylune\.chat\.debug.*has died|ANR in app\.xylune\.chat\.debug' "$OUT/logcat.txt" "$OUT/logcat-crash.txt"; then
  record_failure "runtimeCrashOrAnr=FAIL"
else
  echo "runtimeCrashOrAnr=PASS" >> "$OUT/qa-summary.txt"
fi

cat "$OUT/qa-summary.txt"
exit "$status"
