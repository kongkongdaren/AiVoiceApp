package com.wen.lib_base.helper.`fun`

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.wen.lib_base.R
import com.wen.lib_base.helper.`fun`.data.AppData
import com.wen.lib_base.utils.L

object AppHelper {

    private lateinit var mContext: Context
    private lateinit var pm: PackageManager

    //所有应用
    private val mAllList = ArrayList<AppData>()

    //所有应用包名
    private lateinit var mAllMarkArray: Array<String>

    //分页View
    val mAllViewList = ArrayList<View>()

    fun initHelp(mContext: Context) {
        this.mContext = mContext

        pm = mContext.packageManager
        loadAllApp()
    }

    private fun loadAllApp() {
        var intent = Intent(Intent.ACTION_MAIN, null)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        val appInfo = pm.queryIntentActivities(intent, 0)
        appInfo.forEachIndexed { _, resolveInfo ->
            val appData = AppData(
                resolveInfo.activityInfo.packageName,
                resolveInfo.loadLabel(pm) as String,
                resolveInfo.loadIcon(pm),
                resolveInfo.activityInfo.name,
                resolveInfo.activityInfo.flags == ApplicationInfo.FLAG_SYSTEM
            )
            mAllList.add(appData)
        }
        L.i("mAllList$mAllList")
        initPagerView()
        //加载商店包名
        mAllMarkArray = mContext.resources.getStringArray(R.array.AppMarketArray)

    }

    private fun initPagerView() {
        for (i in 0 until getPageSize()) {
            val rootView =
                View.inflate(mContext, R.layout.layout_app_manager_item, null) as ViewGroup
            //第一层 线性布局
            for (j in 0 until rootView.childCount) {
                //第二层 六个线性布局
                val childX = rootView.getChildAt(j) as ViewGroup
                // 第三层 四个线性布局
                for (k in 0 until childX.childCount) {
                    //第四层 两个View ImageView TextView
                    val child = childX.getChildAt(k) as ViewGroup
                    val iv = child.getChildAt(0) as ImageView
                    val tv = child.getChildAt(1) as TextView
                    //计算当前下标
                    val index = i * 24 + j * 4 + k
                    if (index < mAllList.size) {
                        //获取数据
                        val data = mAllList[index]
                        tv.text = data.appName
                        iv.setImageDrawable(data.appIcon)
                        child.setOnClickListener {
                            intentApp(data.packName)
                        }
                    }
                }
            }
            mAllViewList.add(rootView)
        }

    }

    //获取页面数量
    fun getPageSize(): Int {
        return mAllList.size / 24 + 1
    }

    //获取非系统应用
    fun getNotSystemApp(): List<AppData> {
        return mAllList.filter { !it.isSystemApp }
    }

    fun launcherApp(appName: String): Boolean {
        if (mAllList.size > 0) {
            mAllList.forEach {
                if (it.appName == appName) {
                    intentApp(it.packName)
                    return true
                }
            }
        }
        return false
    }

    fun unInstallApp(appName: String): Boolean {
        if (mAllList.size > 0) {
            mAllList.forEach {
                if (it.appName == appName) {
                    intentUnInstallApp(it.packName)
                    return true
                }
            }
        }
        return false
    }

    private fun intentUnInstallApp(packageName: String) {
        val uri = Uri.parse("package:$packageName")
        val intent = Intent(Intent.ACTION_DELETE)
        intent.data = uri
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        mContext.startActivity(intent)
    }

    //跳转应用市场
    fun launcherAppStore(appName: String): Boolean {
        mAllList.forEach {
            //如果你包含，说明安装了应用商店
            if (mAllMarkArray.contains(it.packName)) {
                if (mAllList.size > 0) {
                    mAllList.forEach {data->
                        if (data.appName == appName) {
                            intentAppStore(data.packName, it.packName)
                            return true
                        }
                    }
                }

            }
        }
        return false
    }

    private fun intentApp(packageName: String) {
        val intent = pm.getLaunchIntentForPackage(packageName)
        intent?.let {
            it.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            mContext.startActivity(it)
        }
    }

    //跳转应用商店
    fun intentAppStore(packageName: String, markPackageName: String) {
        val uri = Uri.parse("market://details?id=$packageName")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.setPackage(markPackageName)
        mContext.startActivity(intent)
    }

}