package com.example.tube.viewmodel

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2020-02-10 <p/>
 */

import android.view.View
import brigitte.ICommandEventAware
import brigitte.jsonParse
import brigitte.shield.*
import brigitte.string
import com.example.tube.R
import com.example.tube.model.remote.KakaoLocationService
import com.example.tube.model.remote.entity.KakaoLocation
import com.example.tube.ui.map.MapViewModel
import com.google.android.gms.location.LocationRequest
import com.patloew.rxlocation.RxLocation
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.robolectric.RobolectricTestRunner

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2020-02-10 <p/>
 */
@RunWith(RobolectricTestRunner::class)
class MapViewModelTest: BaseRoboViewModelTest<MapViewModel>() {
    companion object {
        const val LAT  = 37.56882858276367
        const val LONG = 126.9896240234375
    }

    @Mock lateinit var api: KakaoLocationService

    var rxLocation: RxLocation = RxLocation(app)
    var locationRequest: LocationRequest = LocationRequest.create().apply { priority =
        LocationRequest.PRIORITY_LOW_POWER
    }

    @Before
    @Throws(Exception::class)
    fun setup() {
        initMock()

        viewmodel = MapViewModel(app, api, rxLocation, locationRequest)
    }

    @Test
    fun defaultUiState() {
        viewmodel.apply {
            viewRefresh.assertEquals(View.GONE)
            viewProgress.assertEquals(View.GONE)
            category.assertEquals("HP8")
            categoryId.assertEquals(0)
        }
    }

    @Test
    fun checkDisableLocation() {
        mockLocation()
        mockDisableLocation()

        viewmodel.apply {
            mockObserver<Pair<String, Any>>(commandEvent).apply {
                initLocationData()

                // GPS 가 활성화 되어 있지 않으면 오류를 전달해야 한다.
                verifyChanged(MapViewModel.CMD_ERROR_LOCATION)
            }
        }
    }

    @Test
    fun checkCenterPoint() {
        viewmodel.apply {
            centerPoint.mockCallback().apply {
                centerPoint(LAT, LONG)
                verifyPropertyChanged()
            }

            println("point : " + centerPoint.get().toString())
            centerPoint.assertEquals(LAT to LONG)
        }
    }

//    @Test
//    fun checkItemSelected() {
//        viewmodel.apply {
//            mockObserver<(Int, Any?) -> Unit>(mapCallback).apply {
//                val geoCood = mock(MapPoint.GeoCoordinate::class.java)
//                geoCood.latitude.mockReturn(LAT)
//                geoCood.longitude.mockReturn(LONG)
//
//                val mapPoint = mock(MapPoint::class.java)
//                mapPoint.mapPointGeoCoord.mockReturn(geoCood)
//
//                mapCallback.value?.invoke(DaumMapView.ITEM_SELECTED,
//                    mapPoint)
//
//                mockObserver<Pair<String, Any>>(commandEvent).apply {
//                    verifyChanged(MapViewModel.CMD_SHOW_CURRENT_LOCATION)
//                }
//            }
//        }
//    }

    @Test
    fun checkDisableNetwork() {
        mockNetwork()
        mockDisableNetwork()

        viewmodel.apply {
            search(LONG.toString(), LAT.toString(), 1)

            mockObserver<Pair<String, Any>>(commandEvent).apply {
                verifyChanged(ICommandEventAware.CMD_TOAST to app.string(R.string.common_network_not_connected))
            }
        }
    }

    @Test
    fun checkLastPage() {
        mockNetwork()
        mockEnableNetwork()

        viewmodel.apply {
            isEnded = true

            search(LONG.toString(), LAT.toString(), 1)

            mockObserver<Pair<String, Any>>(commandEvent).apply {
                verifyChanged(ICommandEventAware.CMD_TOAST to app.string(R.string.map_end_page))
            }
        }
    }

    @Test
    fun checkApiSearchCategory() {
        mockReactiveX()
        mockNetwork()
        mockEnableNetwork()

        viewmodel.apply {
            val mockData = """
                {
                  "meta": {
                    "same_name": null,
                    "pageable_count": 11,
                    "total_count": 11,
                    "is_end": true
                  },
                  "documents": [
                    {
                      "place_name": "장생당약국",
                      "distance": "",
                      "place_url": "http://place.map.kakao.com/16618597",
                      "category_name": "의료,건강 > 약국",
                      "address_name": "서울 강남구 대치동 943-16",
                      "road_address_name": "서울 강남구 테헤란로84길 17",
                      "id": "16618597",
                      "phone": "02-558-5476",
                      "category_group_code": "PM9",
                      "category_group_name": "약국",
                      "x": "127.05897078335246",
                      "y": "37.506051888130386"
                    }
                  ]
                }
            """.trimIndent().jsonParse<KakaoLocation>()

            api.searchCategory(category, LONG.toString(), LAT.toString(), 1).mockReturn(
                Single.just(mockData))

            search(LONG.toString(), LAT.toString(), 1)

            items.get()?.apply {
                size.assertEquals(1)
                get(0).apply {
                    title.assertEquals("장생당약국")
                    id.assertEquals("16618597")
                    address.assertEquals("서울 강남구 테헤란로84길 17")
                }
            }

            viewProgress.assertEquals(View.GONE)
            isEnded.assertTrue()
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // MOCK
    //
    ////////////////////////////////////////////////////////////////////////////////////

    override fun initMock() {
        super.initMock()

        mockPermissions(android.Manifest.permission.ACCESS_FINE_LOCATION)
    }
}