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
        sourceCompatibility= JavaVersion.VERSION_1_8
        targetCompatibility=JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget ="1.8"
    }
}

dependencies {

    api(DependenciesConfig.KTX_CORE)
    api(DependenciesConfig.APP_COMPAT)
    api(DependenciesConfig.MATERIAL)
    api(DependenciesConfig.EVENT_BUS)
    api(DependenciesConfig.AROUTER_API)
    api(DependenciesConfig.RECYCLERVIEW)
    api(DependenciesConfig.AND_PERMISSIONS)
    api(DependenciesConfig.VIEWPAGER)
    //Lottie
    api(DependenciesConfig.LOTTIE)
    //刷新
    api(DependenciesConfig.REFRESH_KERNEL)
    api(DependenciesConfig.REFRESH_HEADER)
    api(DependenciesConfig.REFRESH_FOOT)
    //图表
    api(DependenciesConfig.CHART)




    api(project(":lib_voice"))
    api(project(":lib_network"))
    api(files("libs/BaiduLBS_Android.jar"))
    api(files("libs/IndoorscapeAlbumPlugin.jar"))


}