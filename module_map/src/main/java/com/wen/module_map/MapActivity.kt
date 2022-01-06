package com.wen.module_map

import android.Manifest
import com.alibaba.android.arouter.facade.annotation.Route
import com.baidu.mapapi.search.core.PoiInfo
import com.baidu.mapapi.search.poi.PoiResult
import com.wen.lib_base.base.BaseActivity
import com.wen.lib_base.helper.ARouterHelper
import com.wen.lib_base.map.MapManager
import com.wen.lib_base.utils.L
import kotlinx.android.synthetic.main.activity_map.*

@Route(path = ARouterHelper.PATH_MAP)
class MapActivity : BaseActivity() {

    //权限
    val permission = arrayOf(

        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
    )

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
    private fun startLocation(){
        MapManager.setLocationSwitch(true,object:MapManager.OnLocationResultListener{
            override fun result(
                la: Double,
                lo: Double,
                city: String,
                address: String,
                desc: String
            ) {
                MapManager.setCenterMap(la,lo)
                MapManager.searchNearby("美食",la,lo,10,null)
                L.i("定位成功"+address+city)
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