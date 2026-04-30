plugins {
    alias(libs.plugins.android.application)
//    id("com.chaquo.python") version "15.0.1"
}

android {
    namespace = "com.example.loginscreen"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.loginscreen"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
//        ndk {
//            // On Apple silicon, you can omit x86_64.
//            abiFilters += listOf("arm64-v8a", "x86_64")
//        }
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
//    aaptOptions{
//        noCompress = "tflite"
//    }

    compileOptions {
        //sourceCompatibility = JavaVersion.VERSION_1_8
        //targetCompatibility = JavaVersion.VERSION_1_8
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        viewBinding = true
    }
//    flavorDimensions += "pyVersion"
//    productFlavors {
//        create("py38") { dimension = "pyVersion" }
//    }

}
//chaquopy {
//    defaultConfig {
//
//        //buildPython(":/Users/Milan Raut/AppData/Local/Programs/Python/Python311/python.exe")
//        buildPython("C:/Users/Milan Raut/AppData/Local/Programs/Python/Python38/python.exe")
//
//        pip {
//            // A requirement specifier, with or without a version number:
//            install("numpy")
//            install("pillow")
//            install("opencv-python")
//        }
//    }
//    productFlavors {
//        getByName("py38") { version = "3.8" }
//    }
//    sourceSets {
//        getByName("main") {
//            srcDir("src/main/python")
//        }
//    }
//}
dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.tensorflow.lite.metadata)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    // CameraX dependencies
    implementation("androidx.core:core-ktx:1.7.0")
    implementation("com.google.mlkit:face-detection:16.0.0")
    implementation("androidx.camera:camera-core:1.1.0")
    implementation("androidx.camera:camera-camera2:1.1.0")
    implementation("androidx.camera:camera-lifecycle:1.1.0")
    implementation("androidx.camera:camera-view:1.0.0-alpha30")
    implementation("androidx.annotation:annotation:1.6.0")
    implementation(project(":opencv"))

    implementation("org.tensorflow:tensorflow-lite:2.12.0")
    implementation("org.tensorflow:tensorflow-lite-gpu:2.12.0") // If you want to use GPU acceleration
    implementation("org.tensorflow:tensorflow-lite-support:0.3.1")
}