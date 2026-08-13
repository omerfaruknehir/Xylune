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

adb exec-out uiautomator dump /dev/tty > "$OUT/ui.xml" 2> "$OUT/ui-error.txt" || true
adb exec-out screencap -p > "$OUT/launch.png" 2> "$OUT/screenshot-error.txt" || true
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

if [[ -s "$OUT/launch.png" ]]; then
  echo "screenshot=PASS" >> "$OUT/qa-summary.txt"
else
  record_failure "screenshot=FAIL"
fi

if grep -q '<hierarchy' "$OUT/ui.xml" 2>/dev/null; then
  echo "uiHierarchy=PASS" >> "$OUT/qa-summary.txt"
else
  record_failure "uiHierarchy=FAIL"
fi

cat "$OUT/qa-summary.txt"
exit "$status"
