package com.example.tube

import android.content.Context
import androidx.multidex.MultiDex
import com.example.tube.di.component.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication

class MainApp: DaggerApplication() {
    override fun onCreate() {
        super.onCreate()
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