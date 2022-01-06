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
     * START_STICKY:当系统内存不足的时候，杀掉了服务，那么在系统内存不再紧张的时候，启动服务
     * START_NOT_STICKY:当系统内存不足的时候，杀掉了服务，直到下一次startService才启动
     * START_REDELIVER_INTENT:重新传递Intent值
     * START_STICKY_COMPATIBILITY:START_STICKY兼容版本，但是它也不能保证系统kill掉服务一定能重启
     */
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        bindNotification()
        return START_STICKY_COMPATIBILITY
    }

    //绑定通知栏
    private fun bindNotification() {
        startForeground(
            1000,
            NotificationHelper.bindVoiceService(getString(R.string.text_voice_run_text))
        )
    }


    //初始化语音服务
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
                L.e("唤醒准备就绪")
                addAiText("唤醒引擎准备就绪")
            }

            override fun asrStartSpeak() {
                L.e("开始说话")
            }

            override fun asrStopSpeak() {
                L.e("结束说话")
                hideWindow()
            }

            override fun wakeUpSuccess(result: JSONObject) {
                L.e("唤醒成功:$result")
                val errorCOde = result.optInt("errorCode")
                if (errorCOde == 0) {
                    val word = result.optString("word")
                    if (word == "小爱同学") {
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
                L.e("发生错误:$text")
            }

        })
    }

    /**
     * 唤醒成功之后的操作
     */
    private fun wakeUpFix() {
        showWindow()
        updateTips(getString(R.string.text_voice_wakeup_tips))
        SoundPoolHelper.play(R.raw.record_start)
        //应答
        val wakeupText = WordsTools.wakeupWords()
        addAiText(wakeupText,
            object : VoiceTTS.OnTTSResultListener {
                override fun ttsEnd() {
                    //开启识别
                    VoiceManager.startAsr()
                }
            })
    }


    private fun showWindow() {
        mLottieAnimationView.playAnimation()
        WindowHelper.show(mFullWindowView)
    }

    //隐藏窗口
    private fun hideWindow() {
        L.i("======隐藏窗口======")
        mHandler.postDelayed({
            WindowHelper.hide(mFullWindowView)
            mLottieAnimationView.pauseAnimation()
            SoundPoolHelper.play(R.raw.record_over)
        }, 2 * 1000)
    }

    //直接隐藏窗口
    private fun hideTouchWindow() {
        L.i("======隐藏窗口======")
        WindowHelper.hide(mFullWindowView)
        mLottieAnimationView.pauseAnimation()
        SoundPoolHelper.play(R.raw.record_over)
        VoiceManager.stopAsr()
    }

    //打开app
    override fun openApp(appName: String) {
        if (!TextUtils.isEmpty(appName)) {
            val isOpen = AppHelper.launcherApp(appName)
            if (isOpen) {
                addAiText("正在为您打开$appName")
            } else {
                addAiText("很抱歉，无法为您打开$appName")
            }
        }
        hideWindow()
    }

    override fun unInstallApp(appName: String) {
        if (!TextUtils.isEmpty(appName)) {
            val isInstall = AppHelper.unInstallApp(appName)
            if (isInstall) {
                addAiText("正在为您卸载$appName")
            } else {
                addAiText("很抱歉，无法为您卸载$appName")
            }
        }
        hideWindow()
    }

    //其他APP
    override fun otherApp(appName: String) {
        //全部跳转应用市场
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


    //返回
    override fun back() {
        addAiText(getString(R.string.text_voice_back_text))
        CommonSettingHelper.back()
        hideWindow()
    }

    //主页
    override fun home() {
        addAiText(getString(R.string.text_voice_home_text))
        CommonSettingHelper.home()
        hideWindow()
    }

    //音量+
    override fun setVolumeUp() {
        addAiText(getString(R.string.text_voice_volume_add))
        CommonSettingHelper.setVolumeUp()
        hideWindow()
    }

    //音量-
    override fun setVolumeDown() {
        addAiText(getString(R.string.text_voice_volume_sub))
        CommonSettingHelper.setVolumeDown()
        hideWindow()
    }

    //退下
    override fun quit() {
        addAiText(WordsTools.quitWords(), object : VoiceTTS.OnTTSResultListener {
            override fun ttsEnd() {
                hideTouchWindow()
            }

        })
    }

    //拨打联系人
    override fun callPhoneForName(name: String) {
        val list = ContactHelper.mContactList.filter { it.phoneName == name }
        if (list.isNotEmpty()) {
            addAiText("正在为您拨打$name", object : VoiceTTS.OnTTSResultListener {
                override fun ttsEnd() {
                    ContactHelper.callPhone(list[0].phoneNumber)
                }

            })

        } else {
            addAiText(getString(R.string.text_voice_no_friend))
        }
        hideWindow()
    }

    //拨打号码
    override fun callPhoneForNumber(phone: String) {
        addAiText("正在为您拨打$phone", object : VoiceTTS.OnTTSResultListener {
            override fun ttsEnd() {
                ContactHelper.callPhone(phone)
            }

        })
    }

    //播放笑话
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
                            //根据Result随机抽取一段笑话进行播放
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
        addAiText("正在为您搜索笑话")
        ARouterHelper.startActivity(ARouterHelper.PATH_JOKE)
        hideWindow()
    }

    //星座时间
    override fun consTellTime(name: String) {
        val text = ConsTellHelper.getConsTellTime(name)
        addAiText(text, object : VoiceTTS.OnTTSResultListener {
            override fun ttsEnd() {

            }

        })

    }

    //星座详情
    override fun consTellInfo(name: String) {
        addAiText("正在为您查询${name}的详情", object : VoiceTTS.OnTTSResultListener {
            override fun ttsEnd() {
                hideWindow()
            }
        })
        ARouterHelper.startActivity(ARouterHelper.PATH_CONSTELLATION, "name", name)

    }

    //机器人
    override fun aiRobot(text: String) {
        //请求机器人回答
        HttpManager.aiRobotChat(text, object : Callback<RobotData> {

            override fun onFailure(call: Call<RobotData>, t: Throwable) {
                addAiText(WordsTools.noAnswerWords())
                hideWindow()
            }

            override fun onResponse(
                call: Call<RobotData>,
                response: Response<RobotData>
            ) {
                L.i("机器人结果:" + response.body().toString())
                if (response.isSuccessful) {
                    response.body()?.let {
                        if (it.intent.code == 10004) {
                            //回答
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

    //查询天气
    override fun queryWeather(city: String) {
        HttpManager.run {
            queryWeather(city,object :Callback<WeatherData>{
                override fun onResponse(call: Call<WeatherData>, response: Response<WeatherData>) {
                   if (response.isSuccessful){
                       response.body()?.let {
                           //填充数据
                           it.result.realtime.apply {
                               //在UI上显示
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
                   addAiText("无法为您查询${city}的天气")
                    hideWindow()
                }

            })
        }
    }

    //天气详情
    override fun queryWeatherInfo(city: String) {
        addAiText(getString(R.string.text_voice_query_weather, city))
        ARouterHelper.startActivity(ARouterHelper.PATH_WEATHER, "city", city)
        hideWindow()
    }
    //周边搜索
    override fun nearByMap(poi: String) {
        addAiText("正在为您搜索周边$poi")
        ARouterHelper.startActivity(ARouterHelper.PATH_MAP,"type","poi","keyword",poi)
        hideWindow()

    }
    //规划-导航
    override fun routeMap(address: String) {
        addAiText("正在为您规划去${address}的路线")
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
     * 添加AI文本
     */
    private fun addAiText(text: String, mOnTTSResultListener: VoiceTTS.OnTTSResultListener) {
        val bean = ChatList(AppConstants.TYPE_AI_TEXT)
        bean.text = text
        baseAddItem(bean)
        VoiceManager.start(text, mOnTTSResultListener)
    }

    /**
     * 添加天气
     */
    private fun addWeather(city:String,wid:String,info:String,temperature:String, mOnTTSResultListener: VoiceTTS.OnTTSResultListener){
        val bean = ChatList(AppConstants.TYPE_AI_WEATHER)
        bean.city =city
        bean.wid=wid
        bean.info=info
        bean.temperature = "$temperature°"
        baseAddItem(bean)
        val text=city+"今天天气"+info+temperature+"。"
        VoiceManager.start(text, mOnTTSResultListener)
    }

    /**
     * 添加基类
     */
    private fun baseAddItem(bean: ChatList) {
        mList.add(bean)
        mChatAdapter.notifyItemInserted(mList.size - 1)
    }

    /**
     * 更新提示语
     */
    private fun updateTips(text: String) {
        tvVoiceTips.text = text
    }

    /**
     * 笑话错误
     */
    private fun jokeError() {
        hideWindow()
        addAiText("很抱歉，未搜索到笑话")
    }

}