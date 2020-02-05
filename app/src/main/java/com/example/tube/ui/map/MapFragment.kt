package com.example.tube.ui.map

import android.Manifest
import android.os.Bundle
import androidx.fragment.app.Fragment
import brigitte.BaseDaggerFragment
import brigitte.di.dagger.scope.FragmentScope
import brigitte.runtimepermission.PermissionParams
import brigitte.runtimepermission.runtimePermissions
import brigitte.viewmodel.SplashViewModel
import com.example.tube.R
import com.example.tube.databinding.MapFragmentBinding
import dagger.Binds
import dagger.android.ContributesAndroidInjector
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
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

    private val splashViewModel: SplashViewModel by activityInject()
    private val map: MapView?
        get() = binding.mapDaumMap.mapView

    override fun initViewBinding() {
        viewModel.initAdapter(R.layout.map_item)
        binding.mapDaumMap.initMapLayout(requireActivity())

        runtimePermissions(PermissionParams(requireActivity(),
            arrayListOf(Manifest.permission.ACCESS_FINE_LOCATION), { reqCode, result ->
                if (logger.isDebugEnabled) {
                    logger.debug("PERMISSION RESULT : $reqCode($result)")
                }

                if (result) {
                    viewModel.initLocationData()
                } else {
                    requireActivity().finish()
                }
            }, 1)
        )
    }

    override fun initViewModelEvents() {

    }

    override fun onCommandEvent(cmd: String, data: Any) {
        when (cmd) {
            MapViewModel.CMD_INIT_LOCATION -> {
                splashViewModel.closeSplash()
            }

            MapViewModel.CMD_CLEAR_ALL_MARKER -> {
                map?.removeAllPOIItems()
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
            binding.mapRadioGroup.check(categoryId)
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