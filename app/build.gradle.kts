plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "com.commonlauncher.nativeplugin"
    compileSdk = 34

    signingConfigs {
        create("releaseBuild") {
            storeFile = file("common_plugin.jks")
            storePassword = "common_plugin"
            keyAlias = "common_plugin"
            keyPassword = "common_plugin"
        }
    }

    defaultConfig {
        applicationId = "com.commonlauncher.nativeplugin"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    // Make Android extract the .so files to disk so the launcher can point
    // the JVM at {nativeLibraryDir}. Without this they stay compressed inside the APK.
    packaging {
        jniLibs {
            useLegacyPackaging = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
        configureEach {
            // App name
            resValue("string", "app_name", "BASS Plugin")

            // Package name suffix
            applicationIdSuffix = ".bass"

            // Description shown in the launcher's plugin list
            manifestPlaceholders["des"] = "Provides libbass (BASS audio library) for Android"

            // JVM environment parameters
            // Makes JNA load its Android (bionic) libjnidispatch.so from this plugin
            // (put the JNA 5.19.1 Android .aar in app/libs/) instead of the glibc one inside the jar.
            // {nativeLibraryDir} is replaced by the launcher with this plugin's native lib path.
            manifestPlaceholders["environment"] = mutableMapOf<String, String>().apply {
                put("jna.boot.library.path", "{nativeLibraryDir}")
                // Read by the patched soundboard jar: where to find libbass*.so
                put("soundboard.bass.dir", "{nativeLibraryDir}")
            }.run {
                buildList {
                    this@run.forEach { (key, value) ->
                        add("-D$key=$value")
                    }
                }.joinToString(" ")
            }

            // Leave empty = all Minecraft versions
            manifestPlaceholders["minMCVer"] = ""
            manifestPlaceholders["maxMCVer"] = ""
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.aar"))))
}
