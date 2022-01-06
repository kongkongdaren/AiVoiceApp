package com.wen.lib_base.map

import android.content.Context
import android.text.TextUtils
import android.util.Log
import com.baidu.location.BDAbstractLocationListener
import com.baidu.location.BDLocation
import com.baidu.mapapi.SDKInitializer
import com.baidu.mapapi.map.BaiduMap
import com.baidu.mapapi.map.MapStatus
import com.baidu.mapapi.map.MapStatusUpdateFactory
import com.baidu.mapapi.map.MapView
import com.baidu.mapapi.model.LatLng
import com.baidu.location.LocationClientOption

import com.baidu.location.LocationClient
import com.baidu.mapapi.search.core.RouteNode.location

import com.baidu.mapapi.map.MyLocationData
import com.baidu.mapapi.search.core.PoiInfo
import com.baidu.mapapi.search.core.SearchResult
import com.baidu.mapapi.search.poi.*
import com.wen.lib_base.utils.L
import com.baidu.mapapi.search.poi.PoiDetailResult

import com.baidu.mapapi.search.poi.PoiIndoorResult

import com.baidu.mapapi.search.poi.PoiDetailSearchResult

import com.baidu.mapapi.search.poi.PoiResult
import com.baidu.mapapi.search.poi.PoiCitySearchOption





object MapManager {

    //最大缩放 4 - 21
    const val MAX_ZOOM: Float = 17f

    private var mMapView: MapView? = null
    private var mBaiduMap: BaiduMap? = null
    private var mPoiSearch: PoiSearch? = null

    //定位客户端
    private lateinit var mLocationClient: LocationClient

    //上下文
    private lateinit var mContext: Context

    //用户的城市
    private var locationCity: String = ""

    //定位对外的回调
    private var mOnLocationResultListener: OnLocationResultListener? = null

    //POI对外的回调
    private var mOnPoiResultListener: OnPoiResultListener? = null

    //初始化
    fun initMap(mContext: Context) {
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        SDKInitializer.initialize(mContext);
        mLocationClient = LocationClient(mContext)
        //POI
        initPOI()
        //定位
        initLocation()
    }

    fun bindMapView(mMapView: MapView) {
        this.mMapView = mMapView
        mBaiduMap = mMapView.map
        //默认缩放
        zoomMap(MAX_ZOOM)
//        //默认卫星地图
//        setMapType(1)
//        //默认打开交通图
//        setTrafficEnabled(true)
//        //默认开启热力图
//        setBaiduHeatMapEnabled(true)
        //默认开启定位
        setMyLocationEnabled(true)

    }

    //==========================操作方法===========================
    //缩放地图
    fun zoomMap(value: Float) {
        val builder = MapStatus.Builder()
        builder.zoom(value)
        mBaiduMap?.setMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()))
    }

    //设置默认中心点
    fun setCenterMap(la: Double, lo: Double) {
        val latLng = LatLng(la, lo)
        mBaiduMap?.setMapStatus(MapStatusUpdateFactory.newLatLng(latLng))

    }

    //设置实时路况开关
    fun setTrafficEnabled(isOpen: Boolean) {
        //开启交通图
        mBaiduMap?.isTrafficEnabled = isOpen
    }

    //设置热力图
    fun setBaiduHeatMapEnabled(isOpen: Boolean) {
        mBaiduMap?.isBaiduHeatMapEnabled = isOpen
    }

    //设置定位开关
    fun setMyLocationEnabled(isOpen: Boolean) {
        mBaiduMap?.isMyLocationEnabled = isOpen
    }

    /**
     * 0: MAP_TYPE_NORMAL	普通地图（包含3D地图）
     * 1: MAP_TYPE_SATELLITE	卫星图
     * 2: MAP_TYPE_NONE	空白地图
     */

    fun setMapType(index: Int) {
        mBaiduMap?.mapType =
            if (index == 0) BaiduMap.MAP_TYPE_NORMAL
            else BaiduMap.MAP_TYPE_SATELLITE

    }

    //定位开关
    fun setLocationSwitch(isOpen: Boolean, mOnLocationResultListener: OnLocationResultListener?) {
        if (isOpen) {
            this.mOnLocationResultListener = mOnLocationResultListener
            mLocationClient.start()
        } else {
            mLocationClient.stop()
        }
    }

    private fun initLocation() {
        val option = LocationClientOption()
        option.isOpenGps = true // 打开gps

        option.setCoorType("bd09ll") // 设置坐标类型

        option.setScanSpan(1000)
        //高精度
        option.locationMode = LocationClientOption.LocationMode.Hight_Accuracy
        //可选，设置是否需要地址信息，默认不需要
        option.setIsNeedAddress(true)
        //可选，设置是否需要地址描述
        option.setIsNeedLocationDescribe(true)
        //可选，设置是否需要设备方向结果
        option.setNeedDeviceDirect(true)
        option.isLocationNotify = true
        option.setIgnoreKillProcess(true)
        option.setIsNeedLocationDescribe(true)

        mLocationClient.locOption = option
        mLocationClient.locOption = option
        mLocationClient.registerLocationListener(object : BDAbstractLocationListener() {
            override fun onReceiveLocation(location: BDLocation?) {
                if (location == null || mMapView == null) {
                    return
                }

                if (location.locType == 61 || location.locType == 161) {
                    //设置定位默认中心点
                    //setCenterMap(location.latitude, location.longitude)
                    locationCity=location.city
                    mOnLocationResultListener?.result(
                        location.latitude,
                        location.longitude,
                        location.city,
                        location.addrStr,
                        location.locationDescribe
                    )
                } else {
                    L.i("==>定位失败原因：" + location.locType)
                    mOnLocationResultListener?.fail()
                }

                //停止定位
                setLocationSwitch(false, null)
            }
        })
    }

    //==========================生命周期===========================
    fun onResume() {
        mMapView?.onResume()
    }

    fun onPause() {
        mMapView?.onPause()
    }

    fun onDestroy() {
        mMapView?.onDestroy()
        mMapView = null
        mLocationClient.stop();
        mPoiSearch?.destroy()
        mBaiduMap?.isMyLocationEnabled = false
    }
    //===========================POI===========================
   //POI覆盖物
    fun setPoiImage (poiResult:PoiResult){
        mBaiduMap?.clear()

        //创建PoiOverlay对象
        val poiOverlay = PoiOverlay(mBaiduMap)

        //设置Poi检索数据
        poiOverlay.setData(poiResult)

        //将poiOverlay添加至地图并缩放至合适级别
        poiOverlay.addToMap()
        poiOverlay.zoomToSpan()
    }
   private fun initPOI() {
        mPoiSearch = PoiSearch.newInstance()
        mPoiSearch?.setOnGetPoiSearchResultListener(object : OnGetPoiSearchResultListener {
            override fun onGetPoiResult(poiResult: PoiResult?) {


                poiResult?.let {
                    if (it.error === SearchResult.ERRORNO.NO_ERROR) {
                        mOnPoiResultListener?.result(it)
                        //在地图上处理覆盖物
                        setPoiImage(it)

                    }

                }

            }
            override fun onGetPoiDetailResult(poiDetailSearchResult: PoiDetailSearchResult?) {

            }
            override fun onGetPoiIndoorResult(poiIndoorResult: PoiIndoorResult?) {

            }

            //废弃
            override fun onGetPoiDetailResult(poiDetailResult: PoiDetailResult?) {

            }
        })
    }

   private fun poi(keyword: String, city: String,size: Int){
       mPoiSearch?.searchInCity(
           PoiCitySearchOption()
               .city(city) //必填
               .keyword(keyword) //必填
               .pageCapacity(size)
       )
    }

    fun poiSearch (keyword: String, city: String,size: Int,mOnPoiResultListener: OnPoiResultListener?){
        this.mOnPoiResultListener=mOnPoiResultListener
        if (!TextUtils.isEmpty(city)){
             poi(keyword,city,size)
        }else{
            if (!TextUtils.isEmpty(locationCity)){
                //定位过，有数据
                poi(keyword,locationCity,size)
            }else{
                setLocationSwitch(true,object :OnLocationResultListener{
                    override fun result(
                        la: Double,
                        lo: Double,
                        city: String,
                        address: String,
                        desc: String
                    ) {
                        poi(keyword,city,10)
                    }

                    override fun fail() {
                    }
                })

            }
        }

    }
    //搜索周边
    fun searchNearby(
        keyword: String,
        la: Double,
        lo: Double,
        size:Int,
        mOnPoiResultListener: OnPoiResultListener?
    ) {
        this.mOnPoiResultListener = mOnPoiResultListener
        mPoiSearch?.searchNearby(
            PoiNearbySearchOption()
                .location(LatLng(la, lo))
                .radius(500)
                .keyword(keyword)
                .pageCapacity(size)
        )
    }
    //===========================Open impl===========================

    interface OnLocationResultListener {

        fun result(la: Double, lo: Double, city: String, address: String, desc: String)

        fun fail()
    }

    interface OnPoiResultListener {

        fun result(result:PoiResult)


    }

}