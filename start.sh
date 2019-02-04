#!/bin/bash
sdkmanager "system-images;android-21;default;armeabi-v7a" && echo "no" | avdmanager create avd -n test -k "system-images;android-21;default;armeabi-v7a"
export LD_LIBRARY_PATH=${ANDROID_HOME}/emulator/lib64:${ANDROID_HOME}/emulator/lib64/qt/lib
emulator -avd test -no-window -noaudio -no-boot-anim -no-window -accel on &
circle-android wait-for-boot
adb shell input keyevent 82
./gradlew :app:connectedDebugAndroidTest
./gradlew testDebugUnitTest