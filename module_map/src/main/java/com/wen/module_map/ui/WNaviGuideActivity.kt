package com.wen.module_map.ui

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.android.arouter.facade.annotation.Route
import com.baidu.mapapi.walknavi.WalkNavigateHelper
import com.wen.lib_base.helper.ARouterHelper
import com.wen.lib_voice.tts.manager.VoiceManager
import com.baidu.platform.comapi.walknavi.WalkNaviModeSwitchListener

import com.baidu.mapapi.walknavi.adapter.IWNaviStatusListener





@Route(path = ARouterHelper.PATH_MAP_NAVI)
class WNaviGuideActivity: Activity() {

    private lateinit var mNaviHelper: WalkNavigateHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //获取WalkNavigateHelper实例
        mNaviHelper = WalkNavigateHelper.getInstance()
        //获取诱导页面地图展示View
        val view: View = mNaviHelper.onCreate(this@WNaviGuideActivity)
        if (view != null) {
            setContentView(view)
        }
        mNaviHelper.startWalkNavi(this@WNaviGuideActivity)
        //TTS
        mNaviHelper.setTTsPlayer { text, _ ->
            VoiceManager.start(text)
            0
        }
        //AR切换
        mNaviHelper.setWalkNaviStatusListener(object : IWNaviStatusListener {
            /**
             * 普通步行导航模式和步行AR导航模式的切换
             * @param i 导航模式
             * @param walkNaviModeSwitchListener 步行导航模式切换的监听器
             */
            override fun onWalkNaviModeChange(
                mode: Int,
                walkNaviModeSwitchListener: WalkNaviModeSwitchListener
            ) {
                mNaviHelper.switchWalkNaviMode(
                    this@WNaviGuideActivity,
                    mode,
                    walkNaviModeSwitchListener
                )
            }

            override fun onNaviExit() {}
        })
    }


    override fun onResume() {
        super.onResume()
        mNaviHelper.resume()
    }

    override fun onPause() {
        super.onPause()
        mNaviHelper.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mNaviHelper.quit()
    }
}