package com.wen.module_voice_setting

import android.widget.SeekBar
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.wen.lib_base.base.BaseActivity
import com.wen.lib_base.base.adapter.CommonAdapter
import com.wen.lib_base.base.adapter.CommonViewHolder
import com.wen.lib_base.helper.ARouterHelper
import com.wen.lib_voice.tts.manager.VoiceManager
import kotlinx.android.synthetic.main.activity_voice_setting.*

@Route(path = ARouterHelper.PATH_VOICE_SETTING)

class VoiceSettingActivity : BaseActivity() {
    private val mList: ArrayList<String> = ArrayList()

    private var mTtsPeopleIndex: Array<String>? = null

    override fun getLayoutId(): Int {
        return R.layout.activity_voice_setting
    }

    override fun getTitleText(): String {
        return getString(com.wen.lib_base.R.string.app_title_voice_setting)
    }

    override fun initView() {
        bar_voice_speed.progress = 5
        bar_voice_volume.progress = 5

        initData()
        initListener()
        initPeopleView()
        btn_test.setOnClickListener {
            VoiceManager.start("大家好我是小爱")
        }


    }

    private fun initData() {
        val mTtsPeople = resources.getStringArray(R.array.TTSPeople)
        mTtsPeopleIndex = resources.getStringArray(R.array.TTSPeopleIndex)
        mTtsPeople.forEach {
            mList.add(it)
        }
    }

    private fun initPeopleView() {
        rv_voice_people.layoutManager = LinearLayoutManager(this)
        rv_voice_people.addItemDecoration(DividerItemDecoration(this,DividerItemDecoration.VERTICAL))
        rv_voice_people.adapter =
            CommonAdapter(mList, object : CommonAdapter.OnBindDataListener<String> {
                override fun onBindViewHolder(
                    model: String,
                    viewHolder: CommonViewHolder,
                    type: Int,
                    position: Int
                ) {
                    viewHolder.setText(R.id.mTtsPeopleContent,model)
                    viewHolder.itemView.setOnClickListener {
                        mTtsPeopleIndex?.let {
                            VoiceManager.setPeople(it[position])
                        }

                    }
                }

                override fun getLayoutId(type: Int): Int {
                     return R.layout.layout_tts_people
                }

            })


    }

    private fun initListener() {
        bar_voice_speed.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                bar_voice_speed.progress = progress
                VoiceManager.setVoiceSpeed(progress.toString())
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })
        bar_voice_volume.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                bar_voice_volume.progress = progress
                VoiceManager.setVoiceVolume(progress.toString())
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })
    }

    override fun isShowBack(): Boolean {
        return true
    }
}