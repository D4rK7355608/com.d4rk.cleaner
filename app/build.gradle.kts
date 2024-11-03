plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.googlePlayServices)
    alias(libs.plugins.googleOssServices)
    alias(libs.plugins.googleFirebase)
    alias(libs.plugins.compose.compiler)
}

android {
    compileSdk = 35
    namespace = "com.d4rk.cleaner"
    defaultConfig {
        applicationId = "com.d4rk.cleaner"
        minSdk = 23
        targetSdk = 35
        versionCode = 142
        versionName = "3.0.1"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        resourceConfigurations += listOf(
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
            isMinifyEnabled = true
            isShrinkResources = true
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

    //AndroidX
    implementation(dependencyNotation = libs.androidx.core.ktx)
    implementation(dependencyNotation = libs.androidx.appcompat)
    implementation(dependencyNotation = libs.androidx.core.splashscreen)
    implementation(dependencyNotation = libs.androidx.multidex)
    implementation(dependencyNotation = libs.androidx.work.runtime.ktx)

    // Compose
    implementation(dependencyNotation = platform(libs.androidx.compose.bom))
    implementation(dependencyNotation = libs.androidx.ui)
    implementation(dependencyNotation = libs.androidx.activity.compose)
    implementation(dependencyNotation = libs.androidx.constraintlayout.compose)
    implementation(dependencyNotation = libs.androidx.ui.graphics)
    implementation(dependencyNotation = libs.androidx.compose.runtime)
    implementation(dependencyNotation = libs.androidx.runtime.livedata)
    implementation(dependencyNotation = libs.androidx.runtime.rxjava2)
    implementation(dependencyNotation = libs.androidx.ui.tooling.preview)
    implementation(dependencyNotation = libs.androidx.material3)
    implementation(dependencyNotation = libs.androidx.material.icons.extended)
    implementation(dependencyNotation = libs.datastore.preferences)
    implementation(dependencyNotation = libs.androidx.datastore.preferences)
    implementation(dependencyNotation = libs.androidx.foundation)
    implementation(dependencyNotation = libs.androidx.navigation.compose)

    // Lifecycle
    implementation(dependencyNotation = libs.kotlinx.coroutines.android)
    implementation(dependencyNotation = libs.kotlinx.serialization.json)
    implementation(dependencyNotation = libs.androidx.lifecycle.runtime.ktx)
    implementation(dependencyNotation = libs.androidx.lifecycle.common.java8)
    implementation(dependencyNotation = libs.androidx.lifecycle.livedata.ktx)
    implementation(dependencyNotation = libs.androidx.lifecycle.process)
    implementation(dependencyNotation = libs.androidx.lifecycle.viewmodel.ktx)
    implementation(dependencyNotation = libs.androidx.lifecycle.viewmodel.compose)
    implementation(dependencyNotation = libs.androidx.lifecycle.runtime.compose)

    // Google
    implementation(dependencyNotation = libs.play.services.ads)
    implementation(dependencyNotation = libs.billing)
    implementation(dependencyNotation = libs.play.services.oss.licenses)
    implementation(dependencyNotation = libs.material)
    implementation(dependencyNotation = libs.app.update.ktx)
    implementation(dependencyNotation = libs.review.ktx)
    implementation(dependencyNotation = libs.volley)

    // Firebase
    implementation(dependencyNotation = platform(libs.firebase.bom))
    implementation(dependencyNotation = libs.firebase.analytics.ktx)
    implementation(dependencyNotation = libs.firebase.crashlytics.ktx)
    implementation(dependencyNotation = libs.firebase.perf)

    // Image Compression
    implementation(dependencyNotation = libs.compressor)
    implementation(dependencyNotation = libs.coil.compose)
    implementation(dependencyNotation = libs.coil.video)

    // Test
    testImplementation(dependencyNotation = libs.junit)
    androidTestImplementation(dependencyNotation = libs.androidx.junit)
    androidTestImplementation(dependencyNotation = libs.androidx.espresso.core)
    androidTestImplementation(dependencyNotation = libs.ui.test.junit4)
    debugImplementation(dependencyNotation = libs.androidx.ui.tooling)
    debugImplementation(dependencyNotation = libs.androidx.ui.test.manifest)
}