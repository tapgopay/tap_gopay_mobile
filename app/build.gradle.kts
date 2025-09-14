plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.tapgopay"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.tapgopay"
        minSdk = 26
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

            buildConfigField("String", "REMOTE_URL", "\"http://192.168.0.106:5000\"")
            buildConfigField(
                "String",
                "ANDROID_API_KEY",
                "\"MFNFK2dKaSk2ZlwrfDA4Ly9kM3Bqa2JeVnoraCVrRCw=\""
            )
        }

        debug {
            buildConfigField("String", "REMOTE_URL", "\"http://192.168.0.106:5000\"")
            buildConfigField(
                "String",
                "ANDROID_API_KEY",
                "\"MFNFK2dKaSk2ZlwrfDA4Ly9kM3Bqa2JeVnoraCVrRCw=\""
            )
        }

        buildFeatures {
            buildConfig = true
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
        buildConfig = true
    }
    packagingOptions {
        resources {
            excludes += "META-INF/versions/9/OSGI-INF/MANIFEST.MF"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    implementation("androidx.navigation:navigation-compose:2.9.3")

    // Retrofit with Gson serialization converter
    implementation("com.squareup.retrofit2:retrofit:3.0.0")
    implementation("com.squareup.retrofit2:converter-gson:3.0.0")

    // OkHttp3
    implementation("com.squareup.okhttp3:okhttp:5.1.0")
    implementation(libs.androidx.ui.text.google.fonts)

    // Cryptography
    implementation("org.bouncycastle:bcprov-jdk18on:1.78.1")

    // QR Code
    implementation("com.journeyapps:zxing-android-embedded:4.3.0")
    implementation("com.google.zxing:core:3.5.3")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}