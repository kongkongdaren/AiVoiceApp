package com.wen.module_map

import android.Manifest
import android.os.Handler
import com.alibaba.android.arouter.facade.annotation.Route
import com.baidu.mapapi.search.poi.PoiResult
import com.wen.lib_base.base.BaseActivity
import com.wen.lib_base.helper.ARouterHelper
import com.wen.lib_base.map.MapManager
import com.wen.lib_base.utils.L
import com.wen.lib_voice.tts.manager.VoiceManager
import kotlinx.android.synthetic.main.activity_map.*

@Route(path = ARouterHelper.PATH_MAP)
class MapActivity : BaseActivity() {

    //权限
    val permission = arrayOf(

        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.CAMERA,
        Manifest.permission.VIBRATE,
    )
    private val mHandler by lazy { Handler() }
    override fun getLayoutId(): Int {
        return R.layout.activity_map
    }

    override fun getTitleText(): String {
        return getString(com.wen.lib_base.R.string.app_title_map)
    }

    override fun initView() {
        MapManager.bindMapView(mMapView)
        //动态权限
        if (checkPermission(permission)) {
            startLocation()
        } else {
            requestPermission(
                permission
            ) {
                startLocation()
            }
        }
    }

    override fun isShowBack(): Boolean {
        return true
    }

    //开启定位
    private fun startLocation() {
        //获取关键字
        val keyword = intent.getStringExtra("keyword")
        when (intent.getStringExtra("type")) {
            "poi" -> keyword?.let { searchNearByPoi(it) }

            "route" -> keyword?.let { route(it) }
            else -> showMyLocation()
        }
    }

    //显示自身的位置
    private fun showMyLocation() {
        MapManager.setLocationSwitch(true, object : MapManager.OnLocationResultListener {
            override fun result(
                la: Double,
                lo: Double,
                city: String,
                address: String,
                desc: String
            ) {
                //设置中心点
                MapManager.setCenterMap(la, lo)

                L.i("定位成功：" + address + "desc:" + desc)
                //添加覆盖物

            }

            override fun fail() {
                L.i("定位失败")
            }

        })

    }

    //路线规划
    private fun route(address: String) {
        L.i("开始路线规划")
        MapManager.startLocationWalkingSearch(address, object : MapManager.OnNaviResultListener {
            override fun onStartNavi(
                startLa: Double,
                startLo: Double,
                endCity: String,
                address: String
            ) {
                //5S
                VoiceManager.start(getString(R.string.text_start_navi_tts))
                mHandler.postDelayed({
                    MapManager.startCode(
                        endCity,
                        address,
                        object : MapManager.OnCodeResultListener {
                            override fun result(codeLa: Double, codeLo: Double) {
                                L.i("编码成功")
                                MapManager.initNaviEngine(
                                    this@MapActivity,
                                    startLa, startLo,
                                    codeLa, codeLo
                                )
                            }

                        })
                }, 5 * 1000)
            }

        })
    }

    //查找周边POI
    private fun searchNearByPoi(keyword: String) {
        L.i("searchNearByPoi$keyword")
        MapManager.setLocationSwitch(true, object : MapManager.OnLocationResultListener {
            override fun result(
                la: Double,
                lo: Double,
                city: String,
                address: String,
                desc: String
            ) {
                //设置中心点
                MapManager.setCenterMap(la, lo)
                MapManager.searchNearby(
                    keyword,
                    la,
                    lo,
                    10,
                    object : MapManager.OnPoiResultListener {
                        override fun result(result: PoiResult) {
                            //在UI上绘制视图
                        }
                    })
                L.i("定位成功：" + address + "desc:" + desc)
            }

            override fun fail() {
                L.i("定位失败")
            }

        })
    }

    override fun onResume() {
        super.onResume()
        MapManager.onResume()
    }

    override fun onPause() {
        super.onPause()
        MapManager.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        MapManager.onDestroy()
    }


}