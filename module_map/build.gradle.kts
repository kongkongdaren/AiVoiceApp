plugins {
    if (ModuleConfig.isApp) {
        id("com.android.application")
    } else {
        id("com.android.library")
    }
    kotlin("android")
    kotlin("android.extensions")
    kotlin("kapt")
}

android {
    compileSdk= AppConfig.compileSdkVersion

    defaultConfig {
        minSdk=AppConfig.minSdkVersion
        if (ModuleConfig.isApp) {
//            applicationId = ModuleConfig.MODULE_APP_MANAGER
        }
        targetSdk=AppConfig.targetSdkVersion
//        versionCode=AppConfig.versionCode
//        versionName=AppConfig.versionName
//        consumerProguardFiles("consumer-rules.pro")
        //ARouter
        kapt {
            arguments {
                arg("AROUTER_MODULE_NAME", project.name)
            }
        }
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
    //动态替换资源
    sourceSets {
        getByName("main") {
            if (ModuleConfig.isApp) {
                manifest.srcFile("src/main/manifest/AndroidManifest.xml")
            } else {
                manifest.srcFile("src/main/AndroidManifest.xml")
            }
        }
    }
    compileOptions {
        sourceCompatibility= JavaVersion.VERSION_1_8
        targetCompatibility= JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget ="1.8"
    }
}

dependencies {

    kapt(DependenciesConfig.AROUTER_COMPILER)
    api(project(":lib_base"))

}