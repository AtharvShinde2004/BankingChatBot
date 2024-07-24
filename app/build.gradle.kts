plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.bankingchatbot"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.bankingchatbot"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    packaging {
        resources {
            excludes += "META-INF/INDEX.LIST"
            excludes += "META-INF/DEPENDENCIES"
            excludes += "META-INF/NOTICE"
            excludes += "META-INF/LICENSE"
        }
    }
    buildToolsVersion = "35.0.0"
}

dependencies {
    implementation (platform(libs.firebase.bom))
    implementation (libs.firebase.auth)
    implementation (libs.firebase.database)
    implementation (libs.grpc.okhttp)
    implementation (libs.grpc.protobuf)
    implementation (libs.grpc.stub)
    implementation (libs.grpc.auth)
    implementation (libs.google.auth.library.oauth2.http)
    implementation (libs.google.cloud.dialogflow)
    implementation (libs.gax.grpc)
    implementation (libs.google.api.client)
    implementation (libs.gson)
    implementation (libs.appcompat)
    implementation (libs.constraintlayout)
    implementation (libs.material)
    implementation (libs.play.services.auth)

    testImplementation (libs.junit)
    androidTestImplementation (libs.ext.junit)
    androidTestImplementation (libs.espresso.core)
}