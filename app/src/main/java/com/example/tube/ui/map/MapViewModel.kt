package com.example.tube.ui.map

import android.app.Application
import android.view.View
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import brigitte.*
import com.example.tube.R
import com.example.tube.common.widget.DaumMapView
import com.example.tube.model.local.recycler.SearchedData
import com.example.tube.model.mapper.DataMapper
import com.example.tube.model.remote.KakaoLocationService
import com.google.android.gms.location.LocationRequest
import com.patloew.rxlocation.RxLocation
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import org.slf4j.LoggerFactory
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2020-02-05 <p/>
 */

class MapViewModel @Inject constructor(
    app: Application,
    private val api: KakaoLocationService,
    private val rxLocation: RxLocation,
    private val locationRequest: LocationRequest
) : RecyclerViewModel<SearchedData>(app) {

    companion object {
        private val logger = LoggerFactory.getLogger(MapViewModel::class.java)

        const val ITN_REFRESH = "refresh"
        const val ITN_MORE    = "more"

        const val CMD_INIT_LOCATION     = "init-location"
        const val CMD_CLEAR_ALL_MARKER  = "clear-all-marker"
    }

    val viewRefresh  = ObservableInt(View.GONE)
    val viewProgress = ObservableInt(View.GONE)
    val centerPoint  = ObservableField<Pair<Double, Double>>()
    val mapCallback  = ObservableField<(Int, Any?) -> Unit> { type, pt ->
        when (type) {
            DaumMapView.ITEM_SELECTED -> {
                pt?.let {
                    if (it is MapPOIItem) {
                        it.itemName
                    }
                }
            }

            DaumMapView.DRAG_ENDED -> {
                if (viewRefresh.isGone()) {
                    viewRefresh.visible()
                }

                // 현재 위치 갱신
                pt?.let {
                    if (it is MapPoint) {
                        _latitude = it.mapPointGeoCoord.latitude.toString()
                        _longitude = it.mapPointGeoCoord.longitude.toString()
                    }
                }
            }
        }
    }

    val checkedListener = ObservableField<(Int, Int) -> Unit> { id, index ->
        categoryId = id
        category   = when (index) {
            0    -> "HP8"
            1    -> "PM9"
            else -> "OL7"
        }

        if (logger.isDebugEnabled) {
            logger.debug("CATEGORY : $category")
        }

        // 페이지 번호 초기화
        page = 1
        search(_longitude, _latitude, page)
    }

    var category: String = "PM9" //"HP8"
    var categoryId: Int = 0

    private var _longitude: String = ""
    private var _latitude: String = ""

    val longitude: Double
        get() = _longitude.toDouble()
    val latitude: Double
        get() = _latitude.toDouble()

    private var page: Int = 1
    private val dp = CompositeDisposable()


    fun initLocationData() {
        dp.add(rxLocation.location().updates(locationRequest)
            .subscribe({
                _longitude = it.longitude.toString()
                _latitude = it.latitude.toString()

                if (logger.isDebugEnabled) {
                    logger.debug("LOCATION X: $_longitude, Y: $_latitude")
                }

                // 현재 위치로 이동
                centerPoint.set(latitude to longitude)

                command(CMD_INIT_LOCATION)
                search(_longitude, _latitude, page)
            }, ::errorLog))
    }

    fun search(longitude: String, latitude: String, page: Int) {
        if (logger.isDebugEnabled) {
            logger.debug("SEARCH : $page")
        }

        viewProgress.visible()
        dp.add(api.searchCategory(category, longitude, latitude, page)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe ({
                val list = DataMapper.convert(it)

                if (page == 1) {
                    items.set(list)
                } else {
                    if (it.meta.is_end) {
                        viewProgress.gone()
                        toast(R.string.map_end_page)
                        return@subscribe
                    }

                    val newList = items.get()?.toMutableList()
                    newList?.addAll(list)

                    items.set(newList)
                }

                viewProgress.gone()
            }, {
                errorLog(it)
                viewProgress.gone()
            }))
    }

    override fun onCleared() {
        dp.dispose()
        super.onCleared()
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // COMAMND
    //
    ////////////////////////////////////////////////////////////////////////////////////

    override fun command(cmd: String, data: Any) {
        when (cmd) {
            ITN_MORE -> {
                page++
                search(_longitude, _latitude, page)
            }

            ITN_REFRESH -> {
                command(CMD_CLEAR_ALL_MARKER)
                viewRefresh.gone()

                page = 1
                search(_longitude, _latitude, page)
            }

            else -> super.command(cmd, data)
        }
    }
}