package com.example.tube

import android.Manifest
import android.os.Bundle
import androidx.annotation.VisibleForTesting
import brigitte.BaseDaggerActivity
import brigitte.exceptionCatcher
import brigitte.runtimepermission.PermissionParams
import brigitte.runtimepermission.runtimePermissions
import brigitte.viewmodel.SplashViewModel
import com.example.tube.databinding.MainActivityBinding
import com.example.tube.ui.Navigator
import okhttp3.OkHttpClient
import org.slf4j.LoggerFactory
import javax.inject.Inject

class MainActivity : BaseDaggerActivity<MainActivityBinding, MainViewModel>() {
    companion object {
        private val logger = LoggerFactory.getLogger(MainActivity::class.java)
    }

    override val layoutId = R.layout.main_activity

    @Inject lateinit var navigator: Navigator

    private val splashViewModel: SplashViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        exceptionCatcher { logger.error("ERROR: $it") }
        setTheme(R.style.AppTheme)

        super.onCreate(savedInstanceState)

        if (logger.isDebugEnabled) {
            logger.debug("START ACTIVITY")
        }

        if (savedInstanceState == null) {
            navigator.mapFragment()
        }
    }

    override fun initViewBinding() {
    }

    override fun initViewModelEvents() {
        splashViewModel.run {
            observe(closeEvent) {
                binding.activityContainer.removeView(binding.splash)
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // TEST
    //
    ////////////////////////////////////////////////////////////////////////////////////

    // https://github.com/chiuki/espresso-samples/tree/master/idling-resource-okhttp
    @VisibleForTesting
    @Inject lateinit var okhttp: OkHttpClient
}
