package com.wen.module_constellation.fragment

import android.widget.Toast
import com.wen.lib_base.base.BaseFragment
import com.wen.lib_network.HttpManager
import com.wen.lib_network.bean.TodayData
import com.wen.module_constellation.R
import kotlinx.android.synthetic.main.fragment_today.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ToDayFragment(private val isToday: Boolean, val name: String) : BaseFragment(), Callback<TodayData> {
    override fun getLayoutId(): Int {
        return R.layout.fragment_today
    }

    override fun initView() {
        if (isToday) {
            loadToday()
        } else {
            loadTomorrow()
        }
    }

    //加载今天的数据
    private fun loadToday() {
       HttpManager.queryTodayConsTellInfo(name,this)
    }

    //加载明天的数据
    private fun loadTomorrow() {
        HttpManager.queryTomorrowConsTellInfo(name,this)
    }

    override fun onResponse(call: Call<TodayData>, response: Response<TodayData>) {
       val data=  response.body()
        data?.let {
            tvName.text = getString(R.string.text_con_tell_name, it.name)
            tvTime.text = getString(R.string.text_con_tell_time, it.datetime)
            tvNumber.text = getString(R.string.text_con_tell_num, it.number)
            tvFriend.text = getString(R.string.text_con_tell_pair, it.QFriend)
            tvColor.text = getString(R.string.text_con_tell_color, it.color)
            tvSummary.text = getString(R.string.text_con_tell_desc, it.summary)

            pbAll.progress = it.all
            pbHealth.progress = it.health
            pbLove.progress = it.love
            pbMoney.progress = it.money
            pbWork.progress = it.work
        }
    }

    override fun onFailure(call: Call<TodayData>, t: Throwable) {
        Toast.makeText(activity,"加载失败",Toast.LENGTH_LONG).show()
    }

}