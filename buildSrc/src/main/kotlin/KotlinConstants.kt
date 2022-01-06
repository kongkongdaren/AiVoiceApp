object KotlinConstants{
    //Gradle 版本
    const val gradle_version = "7.0.3"

    //Kotlin 版本
    const val kotlin_version = "1.6.0"

}
//应用配置
object AppConfig {

    //依赖版本
    const val compileSdkVersion = 31

    //包名
    const val applicationId = "com.wen.aivoiceapp"

    //最小支持SDK
    const val minSdkVersion =21

    //当前基于SDK
    const val targetSdkVersion = 31

    //版本编码
    const val versionCode = 1

    //版本名称
    const val versionName = "1.0"
}
//依赖配置
object DependenciesConfig {
    //Android标准库
    const val APP_COMPAT = "androidx.appcompat:appcompat:1.2.0"
    //material
    const val MATERIAL = "com.google.android.material:material:1.3.0"
    //Kotlin核心库
    const val KTX_CORE = "androidx.core:core-ktx:1.3.2"
    //EventBus
    const val EVENT_BUS = "org.greenrobot:eventbus:3.2.0"
    //ARouter
    const val AROUTER_API = "com.alibaba:arouter-api:1.5.2"
    const val AROUTER_COMPILER = "com.alibaba:arouter-compiler:1.5.2"
    //recyclerview
    const val RECYCLERVIEW = "androidx.recyclerview:recyclerview:1.2.0"
    //Permissions
    const val AND_PERMISSIONS = "com.yanzhenjie:permission:2.0.3"

    //Retrofit
    const val RETROFIT = "com.squareup.retrofit2:retrofit:2.8.1"
    const val RETROFIT_GSON = "com.squareup.retrofit2:converter-gson:2.8.1"

    //ViewPager
    const val VIEWPAGER = "com.zhy:magic-viewpager:1.0.1"

    //Lottie
    const val LOTTIE = "com.airbnb.android:lottie:3.4.0"

    //刷新
    const val REFRESH_KERNEL = "com.scwang.smart:refresh-layout-kernel:2.0.1"
    const val REFRESH_HEADER = "com.scwang.smart:refresh-header-classics:2.0.1"
    const val REFRESH_FOOT = "com.scwang.smart:refresh-footer-classics:2.0.1"

    //图表
    const val CHART = "com.github.PhilJay:MPAndroidChart:v3.1.0"


}
//Module配置
object ModuleConfig {

    //Module是否App
    var isApp = false

    //包名
    const val MODULE_APP_MANAGER = "com.wen.module_app_manager"
    const val MODULE_CONSTELLATION = "com.wen.module_constellation"
    const val MODULE_DEVELOPER = "com.wen.module_developer"
    const val MODULE_JOKE = "com.wen.module_joke"
    const val MODULE_MAP = "com.wen.module_map"
    const val MODULE_SETTING = "com.wen.module_setting"
    const val MODULE_VOICE_SETTING = "com.wen.module_voice_setting"
   const val MODULE_WEATHER = "com.wen.module_weather"
}