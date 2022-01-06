package com.wen.lib_voice.tts.asr

import android.content.Context
import com.baidu.speech.EventListener
import com.baidu.speech.EventManager
import com.baidu.speech.EventManagerFactory
import com.baidu.speech.asr.SpeechConstant
import org.json.JSONObject


object VoiceAsr {
    //启动数据
    private lateinit var asrJson: String
    private lateinit var asr: EventManager

    fun initAsr(mContext: Context, listener: EventListener) {

        val map = HashMap<Any, Any>()
        map[SpeechConstant.WP_WORDS_FILE] = "assets:///WakeUp.bin"
        map[SpeechConstant.ACCEPT_AUDIO_VOLUME] = true
        map[SpeechConstant.ACCEPT_AUDIO_DATA] = false
        map[SpeechConstant.DISABLE_PUNCTUATION] = false
        map[SpeechConstant.PID] = 15363
        //转换成Json
        asrJson = JSONObject(map as Map<Any, Any>).toString()

        asr = EventManagerFactory.create(mContext, "asr")
        asr.registerListener(listener)
    }


    fun startAsr() {
        asr.send(SpeechConstant.ASR_START, asrJson, null, 0, 0);
    }

    fun stopAsr() {
        asr.send(SpeechConstant.ASR_STOP, null, null, 0, 0);
    }

    fun cancelAsr() {
        asr.send(SpeechConstant.ASR_CANCEL, null, null, 0, 0);
    }

    fun releaseAsr(listener: EventListener) {
        asr.unregisterListener(listener)
    }
}