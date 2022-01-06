package com.wen.lib_voice.tts.engine

import android.util.Log
import com.wen.lib_voice.tts.impl.OnNluResultListener
import com.wen.lib_voice.tts.words.NluWords
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

object VoiceEngineAnalyze {

    private var TAG = VoiceEngineAnalyze::class.java.simpleName
    private lateinit var mOnNluResultListener: OnNluResultListener

    fun analyzeNlu(nlu: JSONObject, mOnNluResultListener: OnNluResultListener) {
        this.mOnNluResultListener = mOnNluResultListener
        val rawText = nlu.optString("raw_text")
        Log.i(TAG, rawText)

        //解析result
        val results = nlu.optJSONArray("results") ?: return
        var nluResultLength = results.length()
        when {
            //说明没有识别结果，机器人登场
            nluResultLength <= 0 -> mOnNluResultListener.aiRobot(rawText)
            //单条命中
            results.length() >= 1 -> analyzeNluSingle(results[0] as JSONObject)

        }

    }

    //处理单条结果
    private fun analyzeNluSingle(result: JSONObject) {
        val domain = result.optString("domain")
        val intent = result.optString("intent")
        val slots = result.optJSONObject("slots")
        slots?.let {
            when (domain) {
                NluWords.NLU_APP -> {
                    when (intent) {
                        NluWords.INTENT_OPEN_APP,
                        NluWords.INTENT_UNINSTALL_APP,
                        NluWords.INTENT_UPDATE_APP,
                        NluWords.INTENT_DOWNLOAD_APP,
                        NluWords.INTENT_SEARCH_APP,
                        NluWords.INTENT_RECOMMEND_APP -> {
                            //得到打开App的名称
                            val userAppName = it.optJSONArray("user_app_name")
                            userAppName?.let { appName ->
                                if (appName.length() > 0) {
                                    val obj = appName[0] as JSONObject
                                    val word = obj.optString("word")
                                    when (intent) {
                                        NluWords.INTENT_OPEN_APP -> {
                                            mOnNluResultListener.openApp(word)
                                        }
                                        NluWords.INTENT_UNINSTALL_APP -> {
                                            mOnNluResultListener.unInstallApp(word)
                                        }
                                        else -> {
                                            //其他App操作
                                            mOnNluResultListener.otherApp(word)
                                        }
                                    }
                                } else {
                                    mOnNluResultListener.nluError()
                                }
                            }
                        }
                        else -> {
                            mOnNluResultListener.nluError()
                        }
                    }


                }
                NluWords.NLU_INSTRUCTION -> {
                    when (intent) {
                        NluWords.INTENT_RETURN -> mOnNluResultListener.back()
                        NluWords.INTENT_BACK_HOME -> mOnNluResultListener.home()
                        NluWords.INTENT_VOLUME_UP -> mOnNluResultListener.setVolumeUp()
                        NluWords.INTENT_VOLUME_DOWN -> mOnNluResultListener.setVolumeDown()


                        else -> mOnNluResultListener.nluError()
                    }
                }
                NluWords.NLU_MOVIE -> {
                    if (NluWords.INTENT_MOVIE_VOL == intent) {
                        val userD = slots.optJSONArray("user_d")
                        userD?.let { user ->
                            if (userD.length() > 0) {
                                val word = (user[0] as JSONObject).optString("word")
                                if (word == "大点") {
                                    mOnNluResultListener.setVolumeUp()
                                } else if (word == "小点") {
                                    mOnNluResultListener.setVolumeDown()
                                }
                            }
                        }

                    } else {
                        mOnNluResultListener.nluError()
                    }
                }
                NluWords.NLU_ROBOT -> {
                    if (NluWords.INTENT_ROBOT_VOLUME == intent) {
                        val volumeControl = slots.optJSONArray("user_volume_control")
                        volumeControl?.let { control ->
                            if (volumeControl.length() > 0) {
                                val word = (control[0] as JSONObject).optString("word")
                                if (word == "大点") {
                                    mOnNluResultListener.setVolumeUp()
                                } else if (word == "小点") {
                                    mOnNluResultListener.setVolumeDown()
                                }
                            }
                        }

                    } else {
                        mOnNluResultListener.nluError()
                    }

                }
                NluWords.NLU_TELEPHONE -> {
                    if (NluWords.INTENT_CALL == intent) {
                        when {
                            slots.has("user_call_target") -> {
                                val callTarget = slots.optJSONArray("user_call_target")
                                callTarget?.let { target ->
                                    if (target.length() > 0) {
                                        val name = (target[0] as JSONObject).optString("word")
                                        mOnNluResultListener.callPhoneForName(name)
                                    } else {
                                        mOnNluResultListener.nluError()
                                    }
                                }

                            }
                            slots.has("user_phone_number") -> {
                                val phoneNumber = slots.optJSONArray("user_phone_number")
                                phoneNumber?.let { number ->
                                    if (number.length() > 0) {
                                        val phone = (number[0] as JSONObject).optString("word")
                                        mOnNluResultListener.callPhoneForNumber(phone)
                                    } else {
                                        mOnNluResultListener.nluError()
                                    }
                                }
                            }
                            else -> {
                                mOnNluResultListener.nluError()
                            }
                        }
                    } else {
                        mOnNluResultListener.nluError()
                    }
                }
                NluWords.NLU_JOKE -> {
                    if (intent == NluWords.INTENT_TELL_JOKE) {
                        mOnNluResultListener.playJoke()
                    } else {
                        mOnNluResultListener.jokeList()
                    }
                }
                NluWords.NLU_SEARCH, NluWords.NLU_NOVEL -> {
                    if (intent == NluWords.INTENT_SEARCH) {
                        mOnNluResultListener.jokeList()
                    } else {
                        mOnNluResultListener.nluError()
                    }
                }
                NluWords.NLU_CONSTELL -> {
                    val consTellNameArray = slots.optJSONArray("user_constell_name")
                    consTellNameArray.let { consTell ->
                        if (consTell.length() > 0) {
                            val wordObject = consTell[0] as JSONObject
                            val word = wordObject.optString("word")
                            when (intent) {
                                NluWords.INTENT_CONSTELL_TIME -> mOnNluResultListener.consTellTime(
                                    word
                                )
                                NluWords.INTENT_CONSTELL_INFO -> mOnNluResultListener.consTellInfo(
                                    word
                                )
                                else -> mOnNluResultListener.nluError()

                            }
                        }

                    }


                }
                NluWords.NLU_WEATHER -> {

                    val userLoc = slots.optJSONArray("user_loc")
                    userLoc?.let { loc ->
                        {
                            if (userLoc.length() > 0) {
                                val locObject = loc[0] as JSONObject
                                val word = locObject.optString("word")
                                if (intent == NluWords.INTENT_USER_WEATHER) {
                                    mOnNluResultListener.queryWeather(word)
                                } else {
                                    mOnNluResultListener.queryWeatherInfo(word)
                                }
                            }
                        }

                    }
                }
                else -> {
                    mOnNluResultListener.nluError()
                }
            }

        }
    }

}