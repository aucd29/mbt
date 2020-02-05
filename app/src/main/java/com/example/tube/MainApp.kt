package com.example.tube

import android.content.Context
import androidx.multidex.MultiDex
import com.example.tube.di.component.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import org.slf4j.LoggerFactory

class MainApp: DaggerApplication() {
    companion object {
        private val logger = LoggerFactory.getLogger(MainApp::class.java)
    }

    override fun onCreate() {
        super.onCreate()

        logger.info("START APP ${BuildConfig.APPLICATION_ID} ${BuildConfig.VERSION_CODE}")
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // DAGGER
    //
    ////////////////////////////////////////////////////////////////////////////////////

    private val component: AndroidInjector<MainApp> by lazy(LazyThreadSafetyMode.NONE) {
        DaggerAppComponent.factory().create(this)
    }

    override fun applicationInjector() =
        component
}