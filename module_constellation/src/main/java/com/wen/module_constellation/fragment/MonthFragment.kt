package com.wen.module_constellation.fragment

import android.widget.Toast
import com.wen.lib_base.base.BaseFragment
import com.wen.lib_base.utils.L
import com.wen.lib_network.HttpManager
import com.wen.lib_network.bean.MonthData
import com.wen.module_constellation.R
import kotlinx.android.synthetic.main.fragment_month.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MonthFragment(val name:String):BaseFragment() {
    override fun getLayoutId(): Int {
        return R.layout.fragment_month
    }

    override fun initView() {
        loadMonthData()
    }

    //加载月份数据
    private fun loadMonthData() {
        HttpManager.queryMonthConsTellInfo(name, object : Callback<MonthData> {
            override fun onFailure(call: Call<MonthData>, t: Throwable) {
                Toast.makeText(activity, getString(R.string.text_load_fail), Toast.LENGTH_LONG)
                    .show()
            }

            override fun onResponse(call: Call<MonthData>, response: Response<MonthData>) {
                val data = response.body()
                data?.let {
                    L.i("it:$it")

                    tvName.text = it.name
                    tvDate.text = it.date
                    tvAll.text = it.all
                    tvHappy.text = it.happyMagic
                    tvHealth.text = it.health
                    tvLove.text = it.love
                    tvMoney.text = it.money
                    tvMonth.text = getString(R.string.text_month_select, it.month)
                    tvWork.text = it.work
                }
            }

        })
    }
}