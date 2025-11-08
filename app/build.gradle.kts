plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.tallericm3"
    compileSdk = 36
    testOptions {
        unitTests.isIncludeAndroidResources = false
    }

    defaultConfig {
        applicationId = "com.example.tallericm3"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        tasks.withType<Test>().configureEach {
            enabled = false
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
        compose = true
    }
}

dependencies {

    // Core
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.activity:activity-compose:1.8.1")

    // Compose
    implementation(platform("androidx.compose:compose-bom:2025.11.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material:material-icons-extended-android:1.5.4")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation(libs.androidx.runtime.saveable)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.lifecycle.process)
    implementation(libs.androidx.foundation)
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")

    testImplementation(libs.junit) // For local unit tests (on JVM)
    androidTestImplementation(libs.androidx.junit) // For instrumented tests (on Android device/emulator)
    androidTestImplementation(libs.androidx.espresso.core) // For instrumented tests
    androidTestImplementation(platform(libs.androidx.compose.bom))
    // Navigation
    implementation("androidx.navigation:navigation-compose:2.7.5")

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:34.5.0"))
    implementation("com.firebaseui:firebase-ui-auth:9.1.1")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-database:21.0.0")
    implementation(libs.firebase.crashlytics.buildtools)
    implementation(libs.firebase.database)

    // Google / Facebook login
    implementation("androidx.credentials:credentials:1.6.0-beta03")
    implementation("androidx.credentials:credentials-play-services-auth:1.6.0-beta03")
    implementation("com.google.android.libraries.identity.googleid:googleid:1.1.1")
    implementation("com.google.android.gms:play-services-auth:21.2.0")
    implementation("com.facebook.android:facebook-android-sdk:18.1.3")
    implementation("com.facebook.android:facebook-login:latest.release")

    // Coil (sin conflictos)
    implementation("io.coil-kt:coil-compose:2.7.0")
    implementation("io.coil-kt:coil:2.4.0")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")

    // Maps
    implementation("com.google.maps.android:maps-compose:4.3.0")
    implementation("com.google.android.gms:play-services-maps:18.1.0")
    implementation("com.google.android.gms:play-services-location:21.2.0")
    implementation("com.google.accompanist:accompanist-permissions:0.36.0")
    implementation("com.google.maps:google-maps-services:0.18.0")
    implementation("com.google.maps.android:android-maps-utils:3.8.0")



    // Volley
    implementation(libs.volley)

    // Cast TV
    implementation(libs.play.services.cast.tv)

    // AndroidX UI text
    implementation(libs.androidx.ui.text)

    // Cloudinary

    implementation(libs.cloudinary.android)// Mantener esta versión Android¿
}
