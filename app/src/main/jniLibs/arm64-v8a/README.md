Place the Android ARM64 BASS native libraries in this directory:

- libbass.so
- libbassmix.so
- libbasswebm.so
- libbassopus.so

These are packaged into the APK under lib/arm64-v8a/ and extracted by Android
because legacy JNI library packaging is enabled in app/build.gradle.kts.
