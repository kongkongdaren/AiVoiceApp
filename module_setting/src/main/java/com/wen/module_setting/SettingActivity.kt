package com.wen.module_setting

import com.alibaba.android.arouter.facade.annotation.Route
import com.wen.lib_base.base.BaseActivity
import com.wen.lib_base.helper.ARouterHelper

@Route(path = ARouterHelper.PATH_SETTING)

class SettingActivity : BaseActivity() {
    override fun getLayoutId(): Int {
        return R.layout.activity_setting
    }

    override fun getTitleText(): String {
        return getString(com.wen.lib_base.R.string.app_title_system_setting)
    }

    override fun initView() {
        TODO("Not yet implemented")
    }

    override fun isShowBack(): Boolean {
        return false
    }
}