package com.example.tube.common.bindingadapter

import androidx.databinding.BindingAdapter
import com.example.tube.common.widget.DaumMapView
import com.example.tube.model.local.recycler.SearchedData
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import org.slf4j.LoggerFactory
import java.lang.StringBuilder

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2020-02-05 <p/>
 */

object DaumMapViewBindingAdapter {
    private val logger = LoggerFactory.getLogger(DaumMapViewBindingAdapter::class.java)

    @JvmStatic
    @BindingAdapter("bindMarker")
    fun bindMarker(view: DaumMapView, dataList: List<SearchedData>?) {
        view.mapView?.let { map ->
            val poiItems = arrayListOf<MapPOIItem>()
            val sb = StringBuilder()
            dataList?.forEach {
                poiItems.add(MapPOIItem().apply{
                    itemName           = it.title
                    mapPoint           = MapPoint.mapPointWithGeoCoord(it.latitude, it.longitude)
                    markerType         = MapPOIItem.MarkerType.BluePin
                    selectedMarkerType = MapPOIItem.MarkerType.RedPin
                })

                sb.append(it.title + ", ")
            }

            if (logger.isDebugEnabled) {
                logger.debug("TITLE (${dataList?.size}): ${sb.toString()}")
            }

            map.addPOIItems(poiItems.toTypedArray())
        }
    }

    @JvmStatic
    @BindingAdapter("bindChangeCenterPoint")
    fun bindMarker(view: DaumMapView, pos: Pair<Double, Double>?) {
        if (pos == null) {
            return
        }

        view.mapView?.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(
            pos.first, pos.second
        ), true)
    }

    @JvmStatic
    @BindingAdapter("bindMapEventCallback")
    fun bindMapEventCallback(view: DaumMapView, callback: ((type: Int, mapPoint: Any?) -> Unit)?) {
        view.callback = callback
    }

//    @JvmStatic
//    @BindingAdapter("bindCurrentLocation")
//    fun bindCurrentLocation(view: DaumMapView) {
//        view.mapView?.let { map ->
//            map.setShowCurrentLocationMarker(true)
//        }
//    }
}