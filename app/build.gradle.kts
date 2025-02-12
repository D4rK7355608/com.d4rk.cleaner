plugins {
    alias(notation= libs.plugins.androidApplication)
    alias(notation= libs.plugins.jetbrainsKotlinAndroid)
    alias(notation= libs.plugins.googlePlayServices)
    alias(notation= libs.plugins.googleFirebase)
    alias(notation= libs.plugins.compose.compiler)
    alias(notation= libs.plugins.about.libraries)
}

android {
    compileSdk = 35
    namespace = "com.d4rk.cleaner"
    defaultConfig {
        applicationId = "com.d4rk.cleaner"
        minSdk = 23
        targetSdk = 35
        versionCode = 155
        versionName = "3.2.2"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        @Suppress("UnstableApiUsage")
        androidResources.localeFilters += listOf(
            "en" ,
            "bg-rBG" ,
            "de-rDE" ,
            "es-rGQ" ,
            "fr-rFR" ,
            "hi-rIN" ,
            "hu-rHU" ,
            "in-rID" ,
            "it-rIT" ,
            "ja-rJP" ,
            "pl-rPL" ,
            "pt-rBR" ,
            "ro-rRO" ,
            "ru-rRU" ,
            "sv-rSE" ,
            "th-rTH" ,
            "tr-rTR" ,
            "uk-rUA" ,
            "zh-rTW" ,
        )
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isDebuggable = false
        }
        debug {
            isDebuggable = true
        }
    }

    buildTypes.forEach { buildType ->
        with(buildType) {
            multiDexEnabled = true
            proguardFiles(
                getDefaultProguardFile(name = "proguard-android-optimize.txt") ,
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        buildConfig = true
        compose = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    bundle {
        storeArchive {
            enable = true
        }
    }
}

dependencies {

    // App Core
    implementation(dependencyNotation = "com.github.D4rK7355608:AppToolkit:0.0.53") {
        isTransitive = true
    }

    implementation(dependencyNotation = libs.androidx.constraintlayout.compose)

    // Image Compression
    implementation(dependencyNotation = libs.compressor)
    implementation(dependencyNotation = libs.coil3.coil.video)
}