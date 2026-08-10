plugins {
    id("com.android.application")
}

android {
    namespace = "app.turp.icontest"
    compileSdk = 36

    defaultConfig {
        applicationId = "app.turp.icontest.v2"
        minSdk = 26
        targetSdk = 36
        versionCode = 2
        versionName = "2.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation("com.android.tools.build:apksig:8.9.1")
}
