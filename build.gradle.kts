// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false

    alias(libs.plugins.daggerHilt) apply false
    alias(libs.plugins.org.jetbrains.kotlin.kapt) apply false
    alias(libs.plugins.google.gms.google.services) apply false
    alias(libs.plugins.google.firebase.crashlytics) apply false
}

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.9.1")
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.44")
        classpath ("androidx.navigation:navigation-safe-args-gradle-plugin:2.5.3")

        // Diğer classpath'ler (AGP, Kotlin vb.)
    }
}