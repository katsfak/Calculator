#!/bin/bash

# Set Android SDK paths
export ANDROID_HOME=$HOME/Android/Sdk
export PATH=$PATH:$ANDROID_HOME/emulator:$ANDROID_HOME/platform-tools

echo "📱 Starting Android Emulator..."
echo ""

# Check if emulator is already running
if adb devices | grep -q "emulator"; then
    echo "✅ Emulator is already running"
else
    echo "🚀 Starting emulator in background..."
    emulator -avd Medium_Phone_API_36.1 -no-snapshot-load > /dev/null 2>&1 &
    echo "⏳ Waiting for emulator to boot (this may take 1-2 minutes)..."
fi

# Wait for device
adb wait-for-device
echo "⏳ Device connected, waiting for boot to complete..."

# Wait for boot to complete
while [ "$(adb shell getprop sys.boot_completed 2>/dev/null | tr -d '\r')" != "1" ]; do
    sleep 2
done

echo "✅ Emulator is ready!"
echo ""
echo "🔨 Building app..."
./gradlew assembleDebug --quiet

echo "📲 Installing Calculator app..."
adb install -r app/build/outputs/apk/debug/app-debug.apk

echo ""
echo "🎉 Done! Your Calculator app should now be installed on the emulator."
echo "📱 Look for 'Calculator' in the app drawer on the emulator."
