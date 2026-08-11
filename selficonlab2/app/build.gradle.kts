plugins { id("com.android.application") }

android {
    namespace = "app.turp.iconlab"
    compileSdk = 36
    defaultConfig {
        applicationId = "app.turp.iconlab"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.1"
    }
    signingConfigs {
        create("self") {
            storeFile = rootProject.file("signing/selfsign.p12")
            storePassword = "iconlab-test-only"
            keyAlias = "iconlab"
            keyPassword = "iconlab-test-only"
            storeType = "pkcs12"
        }
    }
    buildTypes {
        debug { signingConfig = signingConfigs.getByName("self") }
        release {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("self")
        }
    }
    sourceSets {
        getByName("main") {
            java.srcDir("../../selficonlab/app/src/main/java")
            res.srcDir("../../selficonlab/app/src/main/res")
        }
        getByName("debug") {
            res.srcDir("../../selficonlab/app/src/debug/res")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies { implementation("com.android.tools.build:apksig:8.9.1") }
