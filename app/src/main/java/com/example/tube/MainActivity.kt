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

class MainActivity : BaseDaggerActivity<MainActivityBinding, SplashViewModel>() {
    companion object {
        private val logger = LoggerFactory.getLogger(MainActivity::class.java)
    }

    override val layoutId = R.layout.main_activity

    @Inject lateinit var navigator: Navigator

    override fun onCreate(savedInstanceState: Bundle?) {
        exceptionCatcher { logger.error("ERROR: $it") }
        setTheme(R.style.AppTheme)

        super.onCreate(savedInstanceState)

        if (logger.isDebugEnabled) {
            logger.debug("START ACTIVITY")
        }

        // 런타임 퍼미션 확인
        // 확인 되면 map fragment 호출 아니면
        // 안내 팝업창 띄우고 사용자가 직접 퍼미션 승낙을 유도하지만 결국 안하면 앱 종료
        runtimePermissions(PermissionParams(this@MainActivity,
            arrayListOf(Manifest.permission.ACCESS_FINE_LOCATION), { reqCode, result ->
                if (logger.isDebugEnabled) {
                    logger.debug("PERMISSION RESULT : $reqCode($result)")
                }

                if (result) {
                    if (savedInstanceState == null) {
                        navigator.mapFragment()
                        viewModel.initTimeout()
                    }
                } else {
                    finish()
                }
            }, 1)
        )
    }

    override fun initViewBinding() {
    }

    override fun initViewModelEvents() {
        // splash 화면이 종료 되었다고 알리면 main_activity 에 등록되어 있는
        // splash 를 remove view 함
        observe(viewModel.closeEvent) {
            binding.activityContainer.removeView(binding.splash)
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
