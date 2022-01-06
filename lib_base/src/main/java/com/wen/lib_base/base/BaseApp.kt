package com.wen.lib_base.base

import android.app.Application
import android.content.Intent
import com.wen.lib_base.helper.ARouterHelper
import com.wen.lib_base.helper.NotificationHelper
import com.wen.lib_base.map.MapManager
import com.wen.lib_base.service.InitService

open class BaseApp:Application() {

    override fun onCreate() {
        super.onCreate()
        ARouterHelper.initHelper(this)
        startService(Intent(this,InitService::class.java))
        NotificationHelper.initHelper(this)
        MapManager.initMap(this)
    }
}