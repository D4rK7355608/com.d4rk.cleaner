plugins {
    alias(notation= libs.plugins.androidApplication) apply false
    alias(notation= libs.plugins.androidLibrary) apply false
    alias(notation= libs.plugins.jetbrainsKotlinAndroid) apply false
    alias(notation= libs.plugins.compose.compiler) apply false
    alias(notation= libs.plugins.googlePlayServices) apply false
    alias(notation= libs.plugins.googleFirebase) apply false
    alias(notation= libs.plugins.about.libraries) apply true
}