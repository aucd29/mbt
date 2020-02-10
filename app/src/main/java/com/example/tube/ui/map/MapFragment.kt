package com.example.tube.ui.map

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import brigitte.BaseDaggerFragment
import brigitte.alert
import brigitte.di.dagger.scope.FragmentScope
import brigitte.finish
import brigitte.viewmodel.SplashViewModel
import com.example.tube.R
import com.example.tube.databinding.MapFragmentBinding
import dagger.Binds
import dagger.android.ContributesAndroidInjector
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapView
import org.slf4j.LoggerFactory
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2020-02-05 <p/>
 */

class MapFragment @Inject constructor(
): BaseDaggerFragment<MapFragmentBinding, MapViewModel>() {
    companion object {
        private val logger = LoggerFactory.getLogger(MapFragment::class.java)

        private const val STS_MAP_CATEGORY = "map-category"
    }

    override val layoutId = R.layout.map_fragment

    // main 과 viewmodel 공유
    private val splashViewModel: SplashViewModel by activityInject()
    private val map: MapView?
        get() = binding.mapDaumMap.mapView

    override fun initViewBinding() {
        binding.mapDaumMap.apply {
            initMapLayout(requireActivity())
            mapView?.setShowCurrentLocationMarker(false)
        }
    }

    override fun initViewModelEvents() {
        viewModel.apply {
            initAdapter(R.layout.map_item)

            // 아이템 추가 시 자동으로 스크롤 되지 않도록 수정
            adapter.get()?.isScrollToPosition = false
            initLocationData()
        }
    }

    override fun onCommandEvent(cmd: String, data: Any) {
        if (logger.isDebugEnabled) {
            logger.debug("CMD : $cmd")
        }

        MapViewModel.run {
            when (cmd) {
                CMD_INIT_LOCATION ->
                    // viewmodel 에서 현재 location 정보를 얻으면 splash 화면을 종료
                    splashViewModel.closeSplash()

                CMD_ERROR_LOCATION ->
                    // gps 설정이 off 일떄 오류 발생
                    alert(R.string.map_location_not_enabled, listener = { _, _ ->
                        requireActivity().finish()
                    })

                CMD_CLEAR_ALL_MARKER -> {
                    // 새로고침 버튼 선택 시 기존의
                    map?.removeAllPOIItems()
                    binding.mapRecycler.scrollToPosition(0)
                }

                CMD_SHOW_CURRENT_LOCATION ->
                    // 현재 단말 위치를 화면에 표기
                    showCurrentLocation()
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putInt(STS_MAP_CATEGORY, viewModel.categoryId)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)

        savedInstanceState?.let {
            val categoryId = it.getInt(STS_MAP_CATEGORY)

            if (logger.isInfoEnabled) {
                logger.info("RESTORE CATEGORY : $categoryId")
            }

            binding.mapRadioGroup.check(categoryId)
        }
    }

    override fun onDestroyView() {
        hideCurrentLocation()
        super.onDestroyView()
    }

    private fun showCurrentLocation() {
        // binding adapter 로 빼도 되긴하는데 일단 이곳 커스텀 한정이라 일반화 하지 않음
        map?.apply {
            if (logger.isInfoEnabled) {
                logger.info("SHOW CURRENT LOCATION")
            }

            setShowCurrentLocationMarker(true)

            // 너무 눈에 안 띄어서 radius 추가
            setCurrentLocationRadius(50)
            setCurrentLocationRadiusFillColor(Color.argb(37, 255, 0, 0))
            setCurrentLocationRadiusStrokeColor(Color.argb(77, 255, 0, 0))

            setCustomCurrentLocationMarkerTrackingImage(R.drawable.custom_map_present_tracking,
                MapPOIItem.ImageOffset(15, 15))
            setCustomCurrentLocationMarkerDirectionImage(android.R.color.transparent,
                MapPOIItem.ImageOffset(0, 0))
            setCustomCurrentLocationMarkerImage(android.R.color.transparent,
                MapPOIItem.ImageOffset(0, 0))

            // http://apis.map.kakao.com/android/documentation/#MapView_Methods_setCurrentLocationTrackingMode
            currentLocationTrackingMode =
                MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeadingWithoutMapMoving
        }
    }

    private fun hideCurrentLocation() {
        map?.apply {
            if (logger.isDebugEnabled) {
                logger.debug("HIDE CURRENT LOCATION")
            }

            setShowCurrentLocationMarker(false)
            currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOff
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // MODULE
    //
    ////////////////////////////////////////////////////////////////////////////////////

    @dagger.Module
    abstract class Module {
        @FragmentScope
        @ContributesAndroidInjector(modules = [MapFragmentModule::class])
        abstract fun contributeMapFragmentInjector(): MapFragment
    }

    @dagger.Module
    abstract class MapFragmentModule {
        @Binds
        abstract fun bindMapFragment(fragment: MapFragment): Fragment

        @dagger.Module
        companion object {
        }
    }
}