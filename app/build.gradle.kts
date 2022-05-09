plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp") version "1.6.21-1.0.5"
}

android {
    signingConfigs {
        create("release") {
            storeFile = file("../keystore/public")
            storePassword = "123456"
            keyAlias = "public"
            keyPassword = "123456"
        }
    }

    compileSdk = 31

    defaultConfig {
        applicationId = "xyz.nextalone.colorosinstallerplus"
        targetSdk = 31
        minSdk = 31
        buildToolsVersion = "31.0.0"
        versionCode = 1
        versionName = "0.1.0"
    }

    buildTypes {
        named("release") {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("release")
            setProguardFiles(listOf(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"))
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
        freeCompilerArgs = listOf(
                "-Xno-param-assertions",
                "-Xno-call-assertions",
                "-Xno-receiver-assertions"
        )
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // Used 82 API Version
    compileOnly("de.robv.android.xposed:api:82")
    // Implementation API
    implementation("com.highcapable.yukihookapi:api:1.0.87")
    // Implementation Processor
    ksp("com.highcapable.yukihookapi:ksp-xposed:1.0.87")
    implementation("androidx.preference:preference-ktx:1.2.0")
    implementation("androidx.core:core-ktx:1.7.0")
}
