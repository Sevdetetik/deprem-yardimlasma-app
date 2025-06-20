plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services") // Firebase Services
}

android {
    namespace = "com.example.bitrimeproje"
    compileSdk = 35



    defaultConfig {
        applicationId = "com.example.bitrimeproje"
        minSdk = 24
        targetSdk = 35
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
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation ("androidx.recyclerview:recyclerview:1.3.2")
    // Firebase BOM (Bill of Materials) kullanarak Firebase bağımlılıklarını yönetiyoruz
    implementation(platform("com.google.firebase:firebase-bom:33.10.0"))

    // Firebase Authentication
    implementation("com.google.firebase:firebase-auth")
    implementation ("com.google.android.material:material:1.9.0")

    // Firebase Realtime Database
    implementation("com.google.firebase:firebase-database")

    // Google Play Services Authentication (Google ile giriş vb. için)
    implementation("com.google.android.gms:play-services-auth:20.7.0")

    // Ekstra kütüphaneler
    implementation(libs.credentials)
    implementation(libs.credentials.play.services.auth)
    implementation(libs.googleid)
    implementation(libs.firebase.firestore)
// Firebase Cloud Messaging (bildirim için)
    implementation("com.google.firebase:firebase-messaging")

        implementation ("org.eclipse.paho:org.eclipse.paho.client.mqttv3:1.2.5")
        implementation ("org.eclipse.paho:org.eclipse.paho.android.service:1.1.1")

    // Test Kütüphaneleri
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}

