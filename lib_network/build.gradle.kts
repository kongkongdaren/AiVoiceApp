plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("android.extensions")
}

android {
    compileSdk=AppConfig.compileSdkVersion

    defaultConfig {
        minSdk=AppConfig.minSdkVersion
        targetSdk= AppConfig.targetSdkVersion
//        versionCode= AppConfig.versionCode
//        versionName=AppConfig.versionName
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility =JavaVersion.VERSION_1_8
        targetCompatibility =JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget ="1.8"
    }
}

dependencies {

    //Retrofit
    api(DependenciesConfig.RETROFIT)
    api(DependenciesConfig.RETROFIT_GSON)

}