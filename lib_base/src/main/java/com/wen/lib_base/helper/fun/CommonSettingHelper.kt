package com.wen.lib_base.helper.`fun`

import android.app.Instrumentation
import android.content.Context
import android.content.Intent
import android.view.KeyEvent

object CommonSettingHelper {

    private lateinit var mContext: Context
    private lateinit var inst: Instrumentation

    fun initHelper(mContext: Context) {
        this.mContext = mContext

        inst = Instrumentation()
    }

    fun back() {
        Thread { inst.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK) }.start()

    }

    fun home() {
        var intent=Intent(Intent.ACTION_MAIN)
        intent.flags=Intent.FLAG_ACTIVITY_NEW_TASK
        intent.addCategory(Intent.CATEGORY_HOME)
        mContext.startActivity(intent)

    }

    //音量+
    fun setVolumeUp() {
        Thread { inst.sendKeyDownUpSync(KeyEvent.KEYCODE_VOLUME_UP) }.start()


    }

    //音量-
    fun setVolumeDown() {
        Thread { inst.sendKeyDownUpSync(KeyEvent.KEYCODE_VOLUME_DOWN) }.start()

    }
}