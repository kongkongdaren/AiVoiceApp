package com.wen.module_developer

import android.util.Log
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.baidu.speech.EventListener
import com.wen.lib_base.base.BaseActivity
import com.wen.lib_base.base.adapter.CommonAdapter
import com.wen.lib_base.base.adapter.CommonViewHolder
import com.wen.lib_base.helper.ARouterHelper
import com.wen.lib_voice.tts.VoiceTTS.OnTTSResultListener
import com.wen.lib_voice.tts.manager.VoiceManager
import com.wen.module_developer.data.DeveloperListData
import kotlinx.android.synthetic.main.activity_developer.*

@Route(path = ARouterHelper.PATH_DEVELOPER)
class DeveloperActivity : BaseActivity() {

    private val mTypeTitle = 0
    private val mTypeContent = 1;
    private val mList = ArrayList<DeveloperListData>()

    override fun getLayoutId(): Int {
        return R.layout.activity_developer
    }

    override fun getTitleText(): String {
        return getString(com.wen.lib_base.R.string.app_title_developer)
    }

    override fun initView() {
        initData()
        initListView()
    }

    private fun initListView() {
        rvDeveloper.layoutManager = LinearLayoutManager(this);
        rvDeveloper.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        rvDeveloper.adapter =
            CommonAdapter(mList, object : CommonAdapter.OnMoreBindDataListener<DeveloperListData> {
                override fun onBindViewHolder(
                    model: DeveloperListData,
                    viewHolder: CommonViewHolder,
                    type: Int,
                    position: Int
                ) {
                    when (model.type) {
                        mTypeTitle -> {
                            viewHolder.setText(R.id.mTvDeveloperTitle, model.text)
                        }
                        mTypeContent -> {
                            viewHolder.setText(
                                R.id.mTvDeveloperContent,
                                "${position}.${model.text}"
                            )
                            viewHolder.itemView.setOnClickListener {
                                itemClickFun(position)
                            }
                        }
                    }
                }

                override fun getLayoutId(type: Int): Int {
                    return if (type == mTypeTitle) {
                        R.layout.layout_developer_title
                    } else {
                        R.layout.layout_developer_content
                    }
                }

                override fun getItemViewType(position: Int): Int {
                    return mList[position].type
                }

            })
    }

    private fun initData() {
        val dataArray = resources.getStringArray(com.wen.lib_base.R.array.DeveloperListArray)
        dataArray.forEach {
            if (it.contains("[")) {
                addItemData(mTypeTitle, it.replace("[", "").replace("]", ""))
            } else {
                addItemData(mTypeContent, it)
            }
        }
    }

    override fun isShowBack(): Boolean {
        return true
    }

    //添加数据
    private fun addItemData(type: Int, text: String) {
        mList.add(DeveloperListData(type, text))
    }

    //点击事件
    private fun itemClickFun(position: Int) {
        when (position) {
            1 -> ARouterHelper.startActivity(ARouterHelper.PATH_APP_MANAGER)
            2 -> ARouterHelper.startActivity(ARouterHelper.PATH_CONSTELLATION)
            3 -> ARouterHelper.startActivity(ARouterHelper.PATH_JOKE)
            4 -> ARouterHelper.startActivity(ARouterHelper.PATH_MAP)
            5 -> ARouterHelper.startActivity(ARouterHelper.PATH_SETTING)
            6 -> ARouterHelper.startActivity(ARouterHelper.PATH_VOICE_SETTING)
            7 -> ARouterHelper.startActivity(ARouterHelper.PATH_WEATHER)

            9 -> VoiceManager.startAsr()
            10 -> VoiceManager.stopAsr()
            11 -> VoiceManager.cancelAsr()
            12 -> VoiceManager.release()

            14 -> VoiceManager.startWakeUp()
            15 -> VoiceManager.stopWakeUp()


            20 -> VoiceManager.start("您好")
            21 -> {
                //VoiceManager.pause()
                VoiceManager.start("您好，我是小爱同学，很高兴认识你", object : OnTTSResultListener {
                    override fun ttsEnd() {
                        Log.i("test", "ttsEnd")
                    }

                })
            }
            22 -> VoiceManager.resume()
            23 -> VoiceManager.stop()
            24 -> VoiceManager.release()
        }
    }

}