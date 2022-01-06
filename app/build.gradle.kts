plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("android.extensions")
    kotlin("kapt")
}

android {
    compileSdk=AppConfig.compileSdkVersion

    defaultConfig {
        applicationId=AppConfig.applicationId
        minSdk=AppConfig.minSdkVersion
        targetSdk=AppConfig.targetSdkVersion
        versionCode= AppConfig.versionCode
        versionName=AppConfig.versionName

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
    compileOptions {
        sourceCompatibility =JavaVersion.VERSION_1_8
        targetCompatibility =JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    // 签名类型
    signingConfigs {
        register("release") {
            // 别名
            keyAlias = "voice"
            // 别名密码
            keyPassword = "123456"
            // 路径
            storeFile = file("src/main/jks/voice.jks")
            // 签名文件密码
            storePassword = "123456"
        }
    }
    // 输出类型
    android.applicationVariants.all {
        // 编译类型
        val buildType = this.buildType.name
        outputs.all {
            // 判断是否是输出 apk 类型
            if (this is com.android.build.gradle.internal.api.ApkVariantOutputImpl) {
                this.outputFileName = "voice${defaultConfig.versionName}_$buildType.apk"
            }
        }
    }
}

dependencies {


    kapt(DependenciesConfig.AROUTER_COMPILER)

    api(project(":lib_base"))
    api(project(":module_app_manager"))
    api(project(":module_weather"))
    api(project(":module_constellation"))
    api(project(":module_setting"))
    api(project(":module_voice_setting"))
    api(project(":module_joke"))
    api(project(":module_developer"))
    api(project(":module_map"))
}