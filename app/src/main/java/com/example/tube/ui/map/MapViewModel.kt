package com.example.tube.ui.map

import android.app.Application
import android.view.View
import androidx.annotation.VisibleForTesting
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import brigitte.*
import brigitte.widget.viewpager.OffsetDividerItemDecoration
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
 *
 * 요구사항
 * ----
 * 1. 카테고리 에 따라 (병원, 약국, 주유소) 지도 중심 근처로 검색해 다음지도와, 리스트에 표기 (단 장소명, 도로명 주소로 표기)
 * 2. 더 보기 버튼 선택 시 다음 페이지를 로드하고 1 형태로 추가
 * 3. 새로고침을 선택 시 모든 마커와 리스트를 제거 한 뒤 지도 중심위치 기준으로 다시 검색 (단 새로고침 버튼은 지도 이동시에만 화면에 표기)
 * 4. 지도의 처음 위치는 현재 기기 위치이고 마커 를 선택하면 마커가 지도 중심으로 이동 해야 하고 현재 위치는 빨간 점으로 표기해야 함
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

        const val CMD_INIT_LOCATION         = "init-location"
        const val CMD_ERROR_LOCATION        = "error-location"
        const val CMD_CLEAR_ALL_MARKER      = "clear-all-marker"
        const val CMD_SHOW_CURRENT_LOCATION = "show-current-location"
    }

    val viewRefresh  = ObservableInt(View.GONE)
    val viewProgress = ObservableInt(View.GONE)
    val centerPoint  = ObservableField<Pair<Double, Double>>()
    val itemDecoration = ObservableField(OffsetDividerItemDecoration(app, R.drawable.shape_divider_gray, 15))
    val mapCallback  = MutableLiveData<(Int, Any?) -> Unit> { type, pt ->
        when (type) {
            DaumMapView.ITEM_SELECTED -> {
                pt?.let {
                    if (it is MapPOIItem) {
                        it.mapPoint.mapPointGeoCoord.apply {
                            centerPoint(latitude, longitude)
                        }
                    }

                    command(CMD_SHOW_CURRENT_LOCATION)
                }
            }

            DaumMapView.DRAG_ENDED -> {
                if (viewRefresh.isGone()) {
                    viewRefresh.visible()
                }

                // 현재 위치 갱신
                pt?.let {
                    if (it is MapPoint) {
                        _latitude  = it.mapPointGeoCoord.latitude.toString()
                        _longitude = it.mapPointGeoCoord.longitude.toString()
                    }
                }
            }
        }
    }

    // 카테고리 radio 버튼 선택 시 옵저빙 되는 리스너
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

        // 카테고리 변경이니 마커도 초기화
        command(CMD_CLEAR_ALL_MARKER)

        // 페이지 번호 초기화 (radio 버튼을 선택하면 페이지 번호도 초기화 해야 한다.)
        initPage()
        search(_longitude, _latitude, page)
    }

    var category: String = "HP8"
    var categoryId: Int  = 0

    private var _longitude: String = ""
    private var _latitude: String  = ""

    val longitude: Double
        get() = if (_longitude.isEmpty()) 0.0 else _longitude.toDouble()
    val latitude: Double
        get() = if (_latitude.isEmpty()) 0.0 else _latitude.toDouble()

    private var page: Int = 1
    private val dp        = CompositeDisposable()
    private var _isEnded  = false

    private fun initPage() {
        page    = 1
        _isEnded = false
    }

    fun initLocationData() {
        if (!app.isLocationEnabled()) {
            command(CMD_ERROR_LOCATION)
            return
        }

        dp.add(rxLocation.location().updates(locationRequest)
            .subscribe({
                _longitude = it.longitude.toString()
                _latitude  = it.latitude.toString()

                if (logger.isDebugEnabled) {
                    logger.debug("LOCATION X: $_longitude, Y: $_latitude")
                }

                // 현재 위치로 이동
                centerPoint(latitude, longitude)

                initPage()
                command(CMD_INIT_LOCATION)
                search(_longitude, _latitude, page)
            }, ::errorLog))
    }

    fun search(longitude: String, latitude: String, page: Int) {
        if (logger.isDebugEnabled) {
            logger.debug("SEARCH : $page")
        }

        if (!app.isNetworkConntected()) {
            logger.error("ERROR: NETWORK NOT CONNECTED")

            toast(R.string.common_network_not_connected)
            return
        }

        if (_isEnded) {
            toast(R.string.map_end_page)
            return
        }

        viewProgress.visible()
        dp.add(api.searchCategory(category, longitude, latitude, page)
//            .filter(!isEnded)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe ({
                val list = DataMapper.convert(it)

                if (it.meta.is_end) {
                    _isEnded = true
                }

                if (page == 1) {
                    items.set(list)
                } else {
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

    fun centerPoint(latitude: Double, longitude: Double) {
        centerPoint.set(latitude to longitude)
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

                initPage()
                search(_longitude, _latitude, page)
            }

            else -> super.command(cmd, data)
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // TEST
    //
    ////////////////////////////////////////////////////////////////////////////////////

    @VisibleForTesting
    var isEnded: Boolean
        get() = _isEnded
        set(value) {
            _isEnded = value
        }
}