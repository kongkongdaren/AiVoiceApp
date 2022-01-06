package com.wen.lib_base.service

import android.app.IntentService
import android.content.Intent
import android.media.SoundPool
import com.wen.lib_base.helper.ARouterHelper
import com.wen.lib_base.helper.NotificationHelper
import com.wen.lib_base.helper.SoundPoolHelper
import com.wen.lib_base.helper.`fun`.AppHelper
import com.wen.lib_base.helper.`fun`.CommonSettingHelper
import com.wen.lib_base.helper.`fun`.ConsTellHelper
import com.wen.lib_base.map.MapManager
import com.wen.lib_base.utils.AssetsUtils
import com.wen.lib_base.utils.L
import com.wen.lib_base.utils.SpUtils
import com.wen.lib_voice.tts.words.WordsTools

class InitService:IntentService(InitService::class.simpleName) {

    override fun onCreate() {
        super.onCreate()
        L.i("初始化开始")
    }
    override fun onHandleIntent(intent: Intent?) {
        L.i("执行初始化操作")

        SpUtils.initUtils(this)
        WordsTools.initTools(this)
        SoundPoolHelper.init(this)
        AppHelper.initHelp(this)
        CommonSettingHelper.initHelper(this)
        ConsTellHelper.initHelper(this)
        AssetsUtils.initUtils(this)


    }

    override fun onDestroy() {
        super.onDestroy()
        L.i("初始化完成")
    }
}