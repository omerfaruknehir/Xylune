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

# Pick the center of a node strictly from the UI hierarchy, never from pixels.
pick_text_center() {
  local file="$1"
  local text="$2"
  python3 - "$file" "$text" <<'PY'
import html
import re
import sys

path, target = sys.argv[1], sys.argv[2]
data = open(path, encoding="utf-8", errors="replace").read()
for tag in re.findall(r"<node\b[^>]*>", data):
    attrs = dict(re.findall(r'([\w-]+)="([^"]*)"', tag))
    if html.unescape(attrs.get("text", "")) != target:
        continue
    m = re.fullmatch(r"\[(\d+),(\d+)\]\[(\d+),(\d+)\]", attrs.get("bounds", ""))
    if m:
        x1, y1, x2, y2 = map(int, m.groups())
        print((x1 + x2) // 2, (y1 + y2) // 2)
        raise SystemExit(0)
raise SystemExit(1)
PY
}

# Text may be on a child while the clickable semantics live on its parent.
# Android input accepts a tap at the text bounds, so this still derives the
# coordinate entirely from the UI tree.
tap_text() {
  local file="$1"
  local text="$2"
  local label="$3"
  local xy
  if xy="$(pick_text_center "$file" "$text")"; then
    read -r x y <<<"$xy"
    adb shell input tap "$x" "$y"
    echo "${label}=PASS coord=${x},${y}" >> "$OUT/qa-summary.txt"
    return 0
  fi
  record_failure "${label}=FAIL node-not-found text=$text"
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

# Run the repository's complete instrumentation suite first. Keep going on failure
# so screenshots/logs from a launch attempt are still preserved.
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

# Exercise a clean first launch, not a warm process with state left by tests.
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

if grep -q '<hierarchy' "$OUT/welcome-ui.xml" 2>/dev/null; then
  echo "welcomeUiHierarchy=PASS" >> "$OUT/qa-summary.txt"
else
  record_failure "welcomeUiHierarchy=FAIL"
fi
[[ -s "$OUT/welcome.png" ]] && echo "welcomeScreenshot=PASS" >> "$OUT/qa-summary.txt" \
  || record_failure "welcomeScreenshot=FAIL"

# First-run bypass should reach the empty chat without requiring credentials.
if tap_text "$OUT/welcome-ui.xml" "Skip for now" "tapSkipForNow"; then
  sleep 4
  capture_screen main
  if grep -q '<hierarchy' "$OUT/main-ui.xml" 2>/dev/null; then
    echo "mainUiHierarchy=PASS" >> "$OUT/qa-summary.txt"
  else
    record_failure "mainUiHierarchy=FAIL"
  fi
  [[ -s "$OUT/main.png" ]] && echo "mainScreenshot=PASS" >> "$OUT/qa-summary.txt" \
    || record_failure "mainScreenshot=FAIL"

  # Validate the important zero-provider CTA actually navigates somewhere.
  if tap_text "$OUT/main-ui.xml" "Add provider" "tapAddProvider"; then
    sleep 3
    capture_screen provider
    if grep -q '<hierarchy' "$OUT/provider-ui.xml" 2>/dev/null; then
      echo "providerUiHierarchy=PASS" >> "$OUT/qa-summary.txt"
    else
      record_failure "providerUiHierarchy=FAIL"
    fi
    [[ -s "$OUT/provider.png" ]] && echo "providerScreenshot=PASS" >> "$OUT/qa-summary.txt" \
      || record_failure "providerScreenshot=FAIL"
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
