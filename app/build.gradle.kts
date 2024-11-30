plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services") // Correcto
}

android {
    namespace = "com.example.reciclo"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.reciclo"
        minSdk = 31
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

}

dependencies {
    // Dependencias generales
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(platform("com.google.firebase:firebase-bom:33.5.1")) // Firebase BOM para dependencias más limpias

    // Navegación
    implementation("androidx.navigation:navigation-fragment:2.7.1")
    implementation("androidx.navigation:navigation-ui:2.7.1")
    implementation("androidx.cardview:cardview:1.0.0")

    // Firebase
    implementation("com.google.firebase:firebase-analytics") // Firebase Analytics
    implementation("com.google.firebase:firebase-database:20.1.0") // Firebase Database
    implementation("com.google.firebase:firebase-auth:22.1.1") // Firebase Authentication
    implementation("com.google.firebase:firebase-firestore:24.3.0")

    // ML Kit
    implementation("com.google.mlkit:barcode-scanning:17.0.0") // Si usas barcode scanning
    // Para visión general
    implementation("com.google.android.gms:play-services-vision:20.1.3") // Para CameraSource

    // Librerías adicionales
    implementation(libs.navigation.fragment)
    implementation("com.google.android.material:material:1.9.0")
    implementation ("com.google.android.gms:play-services-mlkit-text-recognition:16.0.0")
    implementation(libs.play.services.location)
    implementation ("com.journeyapps:zxing-android-embedded:4.3.0")
    implementation ("com.google.zxing:core:3.4.1")
    implementation ("com.google.android.gms:play-services-auth:20.6.0")

    implementation ("com.google.android.gms:play-services-maps:18.0.2")
    implementation("com.google.android.material:material:1.9.0")
    implementation("com.google.android.gms:play-services-location:21.0.1")
    implementation("com.android.volley:volley:1.2.1")
    implementation("com.squareup.picasso:picasso:2.8")
    implementation ("com.google.code.gson:gson:2.8.9")
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("androidx.recyclerview:recyclerview:1.2.1")

    // Test y pruebas
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}

apply(plugin = "com.google.gms.google-services")
apply(plugin = "com.android.application")