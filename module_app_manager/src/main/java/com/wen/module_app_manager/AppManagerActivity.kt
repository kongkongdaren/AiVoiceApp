package com.wen.module_app_manager

import android.os.Handler
import android.os.Message
import android.view.View
import androidx.viewpager.widget.ViewPager
import com.alibaba.android.arouter.facade.annotation.Route
import com.wen.lib_base.base.BaseActivity
import com.wen.lib_base.base.adapter.BasePagerAdapter
import com.wen.lib_base.helper.ARouterHelper
import com.wen.lib_base.helper.`fun`.AppHelper
import kotlinx.android.synthetic.main.activity_app_manager.*

@Route(path=ARouterHelper.PATH_APP_MANAGER)
class AppManagerActivity : BaseActivity() {

    private val waitApp=1000

    private val mHandler=object:Handler(){
        override fun handleMessage(msg: Message) {
            if(msg.what==waitApp){
                waitAppHandler()
            }
        }
    }
    override fun getLayoutId(): Int {
        return R.layout.activity_app_manager
    }

    override fun getTitleText(): String {
        return getString(com.wen.lib_base.R.string.app_title_app_manager)
    }

    override fun initView() {

        ll_loading.visibility= View.VISIBLE

        waitAppHandler()
    }

    private fun waitAppHandler() {
        if (AppHelper.mAllViewList.size>0){
            initViewPager()
        }else{
            mHandler.sendEmptyMessageAtTime(waitApp,1000)
        }

    }


    override fun isShowBack(): Boolean {
        return true
    }
    private fun initViewPager(){
        mViewPager.offscreenPageLimit=AppHelper.getPageSize()
        mViewPager.adapter=BasePagerAdapter(AppHelper.mAllViewList)
        ll_loading.visibility=View.GONE
        ll_content.visibility=View.VISIBLE
        mPointLayoutView.setPointSize(AppHelper.getPageSize())
        mViewPager.addOnPageChangeListener(object:ViewPager.OnPageChangeListener{
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                mPointLayoutView.setCheck(position)
            }

            override fun onPageScrollStateChanged(state: Int) {
            }

        })

    }

}