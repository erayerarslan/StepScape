plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.daggerHilt)
    alias(libs.plugins.org.jetbrains.kotlin.kapt)
    id("androidx.navigation.safeargs.kotlin")
}

android {
    namespace = "com.erayerarslan.stepscape"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.erayerarslan.stepscape"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true

    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)


    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)

    implementation ("androidx.activity:activity-ktx:1.10.1")

    // Retrofit
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.squareup.retrofit2:converter-moshi:2.9.0")
    implementation("com.squareup.moshi:moshi:1.15.0")
    implementation("com.squareup.moshi:moshi-kotlin:1.15.0")
    kapt("com.squareup.moshi:moshi-kotlin-codegen:1.15.0")
    // OkHttp (isteğe bağlı)
    implementation ("com.squareup.okhttp3:okhttp:4.9.3")
    implementation ("com.squareup.okhttp3:logging-interceptor:4.9.3")
    // (Opsiyonel) Navigation
    implementation ("androidx.navigation:navigation-fragment-ktx:2.8.9")
    implementation ("androidx.navigation:navigation-ui-ktx:2.8.9")

    // RecyclerView
    implementation ("androidx.recyclerview:recyclerview:1.4.0")
    implementation ("com.google.android.material:material:1.12.0") // En güncel sürüm olabilir

    implementation ("com.github.bumptech.glide:glide:4.16.0")
    kapt ("com.github.bumptech.glide:compiler:4.16.0")

    implementation ("io.github.afreakyelf:Pdf-Viewer:2.3.2")

    implementation ("com.airbnb.android:lottie:6.1.0")

    implementation ("com.facebook.shimmer:shimmer:0.5.0")

    implementation ("com.jakewharton.timber:timber:5.0.1")

    implementation ("com.jakewharton.threetenabp:threetenabp:1.4.5")


}

kapt {
    correctErrorTypes = true
}