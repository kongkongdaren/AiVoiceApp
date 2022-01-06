package com.wen.lib_voice.tts.manager

import android.content.Context
import android.util.Log
import com.baidu.speech.EventListener
import com.baidu.speech.asr.SpeechConstant
import com.wen.lib_voice.tts.VoiceTTS
import com.wen.lib_voice.tts.asr.VoiceAsr
import com.wen.lib_voice.tts.impl.OnAsrResultListener
import com.wen.lib_voice.tts.wakeup.VoiceWakeUp
import org.json.JSONObject

object VoiceManager : EventListener {

    private val TAG = VoiceManager::class.simpleName

    private lateinit var mOnAsrResultListener: OnAsrResultListener

    const val VOICE_APP_ID = "25267275"
    const val VOICE_APP_KEY = "ezyw0M70bWkS9QjnW02beQRv"
    const val VOICE_SECRET_KEY = "gqzR0ikqhCGsFhFtUctbYPY0TLySruPi"

    fun initManager(mContext: Context, mOnAsrResultListener: OnAsrResultListener) {
        this.mOnAsrResultListener = mOnAsrResultListener
        VoiceTTS.initTTS(mContext)
        VoiceWakeUp.initWakeUp(mContext, this)
        VoiceAsr.initAsr(mContext, this)
    }

    //-------------------TTS START-------------------
    //播放
    fun start(text: String) {
        VoiceTTS.start(text, null)
    }

    //播放并且监听结果
    fun start(text: String, mOnTTSResultListener: VoiceTTS.OnTTSResultListener) {
        VoiceTTS.start(text, mOnTTSResultListener)
    }

    //暂停
    fun pause() {
        VoiceTTS.pause()
    }

    //继续播放
    fun resume() {
        VoiceTTS.resume()
    }

    //停止播放
    fun stop() {
        VoiceTTS.stop()
    }

    //释放资源
    fun release() {
        VoiceTTS.release()
    }

    //设置发音人
    fun setPeople(people: String) {
        VoiceTTS.setPeople(people)
    }

    //设置语速
    fun setVoiceSpeed(speed: String) {
        VoiceTTS.setVoiceSpeed(speed)
    }

    //设置音量
    fun setVoiceVolume(volume: String) {
        VoiceTTS.setVoiceVolume(volume)
    }

    //-------------------TTS end-------------------

    //-------------------WakeUp start-------------------
    fun startWakeUp() {
        VoiceWakeUp.startWakeUp()
    }

    fun stopWakeUp() {
        VoiceWakeUp.stopWakeUp()
    }


    //-------------------WakeUp end-------------------


    //-------------------ASR START-------------------
    fun startAsr() {
        VoiceAsr.startAsr()
    }

    fun stopAsr() {
        VoiceAsr.stopAsr()
    }

    fun cancelAsr() {
        VoiceAsr.cancelAsr()
    }

    fun releaseAsr() {
        VoiceAsr.releaseAsr(this)
    }


    //-------------------ASR STOP-------------------

    override fun onEvent(
        name: String?,
        params: String?,
        byte: ByteArray?,
        offset: Int,
        length: Int
    ) {

        Log.d(TAG, String.format("event: name=%s, params=%s", name, params));

        name?.let {
            when (name) {
                SpeechConstant.CALLBACK_EVENT_WAKEUP_READY -> mOnAsrResultListener.weakUpReady()
                SpeechConstant.CALLBACK_EVENT_ASR_BEGIN -> mOnAsrResultListener.asrStartSpeak()
                SpeechConstant.CALLBACK_EVENT_ASR_END -> mOnAsrResultListener.asrStopSpeak()

            }
            if (params == null) {
                return
            }
            val allJson = JSONObject(params)
            when (name) {
                SpeechConstant.CALLBACK_EVENT_WAKEUP_SUCCESS -> mOnAsrResultListener.wakeUpSuccess(
                    allJson
                )
                SpeechConstant.CALLBACK_EVENT_WAKEUP_ERROR -> mOnAsrResultListener.voiceError("唤醒失败")
                SpeechConstant.CALLBACK_EVENT_ASR_FINISH -> mOnAsrResultListener.asrResult(allJson)
                SpeechConstant.CALLBACK_EVENT_ASR_PARTIAL -> {
                    mOnAsrResultListener.updateUserText(allJson.optString("best_result"))
                    byte?.let {
                        val nlu = JSONObject(String(byte, offset, length))
                        mOnAsrResultListener.nluResult(nlu)

                    }

                }
                else -> {
                }
            }

        }
    }


}