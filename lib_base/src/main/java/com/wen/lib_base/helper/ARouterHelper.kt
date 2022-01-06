package com.wen.lib_base.helper

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.alibaba.android.arouter.BuildConfig
import com.alibaba.android.arouter.launcher.ARouter

object ARouterHelper {

    const val PATH_APP_MANAGER = "/app_manager/app_manager_activity"
    const val PATH_CONSTELLATION = "/constellation/constellation_activity"
    const val PATH_DEVELOPER = "/developer/developer_activity"
    const val PATH_JOKE = "/joke/joke_activity"
    const val PATH_MAP = "/map/map_activity"
    const val PATH_MAP_NAVI = "/map/navi_activity"
    const val PATH_SETTING = "/setting/setting_activity"
    const val PATH_VOICE_SETTING = "/voice/voice_setting_activity"
    const val PATH_WEATHER = "/weather/weather_activity"


    fun  initHelper(application: Application){
        if (BuildConfig.DEBUG) {           // 这两行必须写在init之前，否则这些配置在init过程中将无效
            ARouter.openLog();     // 打印日志
            ARouter.openDebug();   // 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
        }
        ARouter.init(application); // 尽可能早，推荐在Application中初始化
    }

    fun startActivity(path:String){
        ARouter.getInstance().build(path).navigation()
    }
    fun startActivity(activity:Activity,path:String,requestCode:Int){
        ARouter.getInstance().build(path).navigation(activity,requestCode);
    }

    fun startActivity(path:String,key:String,value:String){
        ARouter.getInstance().build(path)
            .withString(key, value).navigation()

    }
    fun startActivity(path:String,key:String,value:String,key1:String,value1:String){
        ARouter.getInstance().build(path)
            .withString(key, value)
            .withString(key1, value1)
            .navigation()

    }
    fun startActivity(path:String,key:String,value:Int){
        ARouter.getInstance().build(path)
            .withInt(key, value).navigation()

    }
    fun startActivity(path:String,key:String,value:Boolean){
        ARouter.getInstance().build(path)
            .withBoolean(key, value).navigation()

    }
    fun startActivity(path:String,key:String,bundle: Bundle){
        ARouter.getInstance().build(path)
            .withBundle(key, bundle).navigation()

    }
    fun startActivity(path:String,key:String,any: Any){
        ARouter.getInstance().build(path)
            .withObject(key, any).navigation()

    }
}