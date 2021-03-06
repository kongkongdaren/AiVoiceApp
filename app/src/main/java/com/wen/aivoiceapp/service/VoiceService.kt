package com.wen.aivoiceapp.service

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.wen.aivoiceapp.R
import com.wen.aivoiceapp.adapter.ChatListAdapter
import com.wen.aivoiceapp.data.ChatList
import com.wen.aivoiceapp.entity.AppConstants
import com.wen.lib_base.helper.ARouterHelper
import com.wen.lib_base.helper.NotificationHelper
import com.wen.lib_base.helper.SoundPoolHelper
import com.wen.lib_base.helper.WindowHelper
import com.wen.lib_base.helper.`fun`.AppHelper
import com.wen.lib_base.helper.`fun`.CommonSettingHelper
import com.wen.lib_base.helper.`fun`.ConsTellHelper
import com.wen.lib_base.helper.`fun`.ContactHelper
import com.wen.lib_base.map.MapManager
import com.wen.lib_base.utils.L
import com.wen.lib_network.HttpManager
import com.wen.lib_network.bean.JokeOneData
import com.wen.lib_network.bean.RobotData
import com.wen.lib_network.bean.WeatherData
import com.wen.lib_voice.tts.VoiceTTS
import com.wen.lib_voice.tts.engine.VoiceEngineAnalyze
import com.wen.lib_voice.tts.impl.OnAsrResultListener
import com.wen.lib_voice.tts.impl.OnNluResultListener
import com.wen.lib_voice.tts.manager.VoiceManager
import com.wen.lib_voice.tts.words.WordsTools
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VoiceService : Service(), OnNluResultListener {
    private val mHandler = Handler()

    private lateinit var mFullWindowView: View
    private lateinit var mChatListView: RecyclerView
    private lateinit var mLottieAnimationView: LottieAnimationView
    private lateinit var tvVoiceTips: TextView

    private val mList = ArrayList<ChatList>()
    private lateinit var mChatAdapter: ChatListAdapter
    override fun onCreate() {
        super.onCreate()

        initCoreVoiceService()
    }


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    /**
     * START_STICKY:????????????????????????????????????????????????????????????????????????????????????????????????????????????
     * START_NOT_STICKY:??????????????????????????????????????????????????????????????????startService?????????
     * START_REDELIVER_INTENT:????????????Intent???
     * START_STICKY_COMPATIBILITY:START_STICKY?????????????????????????????????????????????kill????????????????????????
     */
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        bindNotification()
        return START_STICKY_COMPATIBILITY
    }

    //???????????????
    private fun bindNotification() {
        startForeground(
            1000,
            NotificationHelper.bindVoiceService(getString(R.string.text_voice_run_text))
        )
    }


    //?????????????????????
    private fun initCoreVoiceService() {

        WindowHelper.initHelper(this)

        mFullWindowView = WindowHelper.getView(R.layout.layout_window_item)

        mChatListView = mFullWindowView.findViewById<RecyclerView>(R.id.mChatListView)
        mLottieAnimationView =
            mFullWindowView.findViewById<LottieAnimationView>(R.id.mLottieAnimationView)
        tvVoiceTips = mFullWindowView.findViewById<TextView>(R.id.tvVoiceTips)
        mChatListView.layoutManager = LinearLayoutManager(this)
        mChatAdapter = ChatListAdapter(mList)
        mChatListView.adapter = mChatAdapter

        VoiceManager.initManager(this, object : OnAsrResultListener {
            override fun weakUpReady() {
                L.e("??????????????????")
                addAiText("????????????????????????")
            }

            override fun asrStartSpeak() {
                L.e("????????????")
            }

            override fun asrStopSpeak() {
                L.e("????????????")
                hideWindow()
            }

            override fun wakeUpSuccess(result: JSONObject) {
                L.e("????????????:$result")
                val errorCOde = result.optInt("errorCode")
                if (errorCOde == 0) {
                    val word = result.optString("word")
                    if (word == "????????????") {
                        wakeUpFix()

                    }
                }
            }

            override fun updateUserText(text: String) {
                updateTips(text)
            }

            override fun asrResult(result: JSONObject) {

            }

            override fun nluResult(nlu: JSONObject) {
                addMineText(nlu.optString("raw_text"))
                VoiceEngineAnalyze.analyzeNlu(nlu, this@VoiceService)

            }


            override fun voiceError(text: String) {
                hideWindow()
                L.e("????????????:$text")
            }

        })
    }

    /**
     * ???????????????????????????
     */
    private fun wakeUpFix() {
        showWindow()
        updateTips(getString(R.string.text_voice_wakeup_tips))
        SoundPoolHelper.play(R.raw.record_start)
        //??????
        val wakeupText = WordsTools.wakeupWords()
        addAiText(wakeupText,
            object : VoiceTTS.OnTTSResultListener {
                override fun ttsEnd() {
                    //????????????
                    VoiceManager.startAsr()
                }
            })
    }


    private fun showWindow() {
        mLottieAnimationView.playAnimation()
        WindowHelper.show(mFullWindowView)
    }

    //????????????
    private fun hideWindow() {
        L.i("======????????????======")
        mHandler.postDelayed({
            WindowHelper.hide(mFullWindowView)
            mLottieAnimationView.pauseAnimation()
            SoundPoolHelper.play(R.raw.record_over)
        }, 2 * 1000)
    }

    //??????????????????
    private fun hideTouchWindow() {
        L.i("======????????????======")
        WindowHelper.hide(mFullWindowView)
        mLottieAnimationView.pauseAnimation()
        SoundPoolHelper.play(R.raw.record_over)
        VoiceManager.stopAsr()
    }

    //??????app
    override fun openApp(appName: String) {
        if (!TextUtils.isEmpty(appName)) {
            val isOpen = AppHelper.launcherApp(appName)
            if (isOpen) {
                addAiText("??????????????????$appName")
            } else {
                addAiText("??????????????????????????????$appName")
            }
        }
        hideWindow()
    }

    override fun unInstallApp(appName: String) {
        if (!TextUtils.isEmpty(appName)) {
            val isInstall = AppHelper.unInstallApp(appName)
            if (isInstall) {
                addAiText("??????????????????$appName")
            } else {
                addAiText("??????????????????????????????$appName")
            }
        }
        hideWindow()
    }

    //??????APP
    override fun otherApp(appName: String) {
        //????????????????????????
        if (!TextUtils.isEmpty(appName)) {
            val isIntent = AppHelper.launcherAppStore(appName)
            if (isIntent) {
                addAiText(getString(R.string.text_voice_app_option, appName))
            } else {
                addAiText(WordsTools.noAnswerWords())
            }
        }
        hideWindow()
    }


    //??????
    override fun back() {
        addAiText(getString(R.string.text_voice_back_text))
        CommonSettingHelper.back()
        hideWindow()
    }

    //??????
    override fun home() {
        addAiText(getString(R.string.text_voice_home_text))
        CommonSettingHelper.home()
        hideWindow()
    }

    //??????+
    override fun setVolumeUp() {
        addAiText(getString(R.string.text_voice_volume_add))
        CommonSettingHelper.setVolumeUp()
        hideWindow()
    }

    //??????-
    override fun setVolumeDown() {
        addAiText(getString(R.string.text_voice_volume_sub))
        CommonSettingHelper.setVolumeDown()
        hideWindow()
    }

    //??????
    override fun quit() {
        addAiText(WordsTools.quitWords(), object : VoiceTTS.OnTTSResultListener {
            override fun ttsEnd() {
                hideTouchWindow()
            }

        })
    }

    //???????????????
    override fun callPhoneForName(name: String) {
        val list = ContactHelper.mContactList.filter { it.phoneName == name }
        if (list.isNotEmpty()) {
            addAiText("??????????????????$name", object : VoiceTTS.OnTTSResultListener {
                override fun ttsEnd() {
                    ContactHelper.callPhone(list[0].phoneNumber)
                }

            })

        } else {
            addAiText(getString(R.string.text_voice_no_friend))
        }
        hideWindow()
    }

    //????????????
    override fun callPhoneForNumber(phone: String) {
        addAiText("??????????????????$phone", object : VoiceTTS.OnTTSResultListener {
            override fun ttsEnd() {
                ContactHelper.callPhone(phone)
            }

        })
    }

    //????????????
    override fun playJoke() {
        HttpManager.queryJoke(object : Callback<JokeOneData> {
            override fun onFailure(call: Call<JokeOneData>, t: Throwable) {
                L.i("onFailure:$t")
                jokeError()
            }

            override fun onResponse(call: Call<JokeOneData>, response: Response<JokeOneData>) {
                L.i("Joke onResponse")
                if (response.isSuccessful) {
                    response.body()?.let {
                        if (it.error_code == 0) {
                            //??????Result????????????????????????????????????
                            val index = WordsTools.randomInt(it.result.size)
                            L.i("index:$index")
                            if (index < it.result.size) {
                                val data = it.result[index]
                                addAiText(data.content, object : VoiceTTS.OnTTSResultListener {
                                    override fun ttsEnd() {
                                        hideWindow()
                                    }
                                })
                            }
                        } else {
                            jokeError()
                        }
                    }
                } else {
                    jokeError()
                }
            }
        })
    }

    override fun jokeList() {
        addAiText("????????????????????????")
        ARouterHelper.startActivity(ARouterHelper.PATH_JOKE)
        hideWindow()
    }

    //????????????
    override fun consTellTime(name: String) {
        val text = ConsTellHelper.getConsTellTime(name)
        addAiText(text, object : VoiceTTS.OnTTSResultListener {
            override fun ttsEnd() {

            }

        })

    }

    //????????????
    override fun consTellInfo(name: String) {
        addAiText("??????????????????${name}?????????", object : VoiceTTS.OnTTSResultListener {
            override fun ttsEnd() {
                hideWindow()
            }
        })
        ARouterHelper.startActivity(ARouterHelper.PATH_CONSTELLATION, "name", name)

    }

    //?????????
    override fun aiRobot(text: String) {
        //?????????????????????
        HttpManager.aiRobotChat(text, object : Callback<RobotData> {

            override fun onFailure(call: Call<RobotData>, t: Throwable) {
                addAiText(WordsTools.noAnswerWords())
                hideWindow()
            }

            override fun onResponse(
                call: Call<RobotData>,
                response: Response<RobotData>
            ) {
                L.i("???????????????:" + response.body().toString())
                if (response.isSuccessful) {
                    response.body()?.let {
                        if (it.intent.code == 10004) {
                            //??????
                            if (it.results.isEmpty()) {
                                addAiText(WordsTools.noAnswerWords())
                                hideWindow()
                            } else {
                                addAiText(it.results[0].values.text)
                                hideWindow()
                            }
                        } else {
                            addAiText(WordsTools.noAnswerWords())
                            hideWindow()
                        }
                    }
                }
            }

        })

    }

    //????????????
    override fun queryWeather(city: String) {
        HttpManager.run {
            queryWeather(city,object :Callback<WeatherData>{
                override fun onResponse(call: Call<WeatherData>, response: Response<WeatherData>) {
                   if (response.isSuccessful){
                       response.body()?.let {
                           //????????????
                           it.result.realtime.apply {
                               //???UI?????????
                               addWeather(city,wid,info,temperature,object:VoiceTTS.OnTTSResultListener{
                                   override fun ttsEnd() {
                                       hideWindow()
                                   }

                               })

                           }
                       }
                   }
                }

                override fun onFailure(call: Call<WeatherData>, t: Throwable) {
                   addAiText("??????????????????${city}?????????")
                    hideWindow()
                }

            })
        }
    }

    //????????????
    override fun queryWeatherInfo(city: String) {
        addAiText(getString(R.string.text_voice_query_weather, city))
        ARouterHelper.startActivity(ARouterHelper.PATH_WEATHER, "city", city)
        hideWindow()
    }
    //????????????
    override fun nearByMap(poi: String) {
        addAiText("????????????????????????$poi")
        ARouterHelper.startActivity(ARouterHelper.PATH_MAP,"type","poi","keyword",poi)
        hideWindow()

    }
    //??????-??????
    override fun routeMap(address: String) {
        addAiText("?????????????????????${address}?????????")
        ARouterHelper.startActivity(ARouterHelper.PATH_MAP,"type","route","keyword",address)

    }


    override fun nluError() {
        addAiText(WordsTools.noAnswerWords())
        hideWindow()
    }

    private fun addMineText(text: String) {
        var bean = ChatList(AppConstants.TYPE_MINE_TEXT)
        bean.text = text
        baseAddItem(bean)

    }

    private fun addAiText(text: String) {
        var bean = ChatList(AppConstants.TYPE_AI_TEXT)
        bean.text = text
        baseAddItem(bean)
    }

    /**
     * ??????AI??????
     */
    private fun addAiText(text: String, mOnTTSResultListener: VoiceTTS.OnTTSResultListener) {
        val bean = ChatList(AppConstants.TYPE_AI_TEXT)
        bean.text = text
        baseAddItem(bean)
        VoiceManager.start(text, mOnTTSResultListener)
    }

    /**
     * ????????????
     */
    private fun addWeather(city:String,wid:String,info:String,temperature:String, mOnTTSResultListener: VoiceTTS.OnTTSResultListener){
        val bean = ChatList(AppConstants.TYPE_AI_WEATHER)
        bean.city =city
        bean.wid=wid
        bean.info=info
        bean.temperature = "$temperature??"
        baseAddItem(bean)
        val text=city+"????????????"+info+temperature+"???"
        VoiceManager.start(text, mOnTTSResultListener)
    }

    /**
     * ????????????
     */
    private fun baseAddItem(bean: ChatList) {
        mList.add(bean)
        mChatAdapter.notifyItemInserted(mList.size - 1)
    }

    /**
     * ???????????????
     */
    private fun updateTips(text: String) {
        tvVoiceTips.text = text
    }

    /**
     * ????????????
     */
    private fun jokeError() {
        hideWindow()
        addAiText("??????????????????????????????")
    }

}