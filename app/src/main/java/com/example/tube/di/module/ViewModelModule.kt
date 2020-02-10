package com.example.tube.di.module

import androidx.lifecycle.ViewModel
import brigitte.di.dagger.module.ViewModelKey
import brigitte.viewmodel.SplashViewModel
import com.example.tube.ui.map.MapViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2018. 12. 6. <p/>
 */

@Module
abstract class ViewModelModule {
    ////////////////////////////////////////////////////////////////////////////////////
    //
    // MAIN
    //
    ////////////////////////////////////////////////////////////////////////////////////

    @Binds
    @IntoMap
    @ViewModelKey(SplashViewModel::class)
    abstract fun bindSplashViewModel(vm: SplashViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MapViewModel::class)
    abstract fun bindMapViewModel(vm: MapViewModel): ViewModel

}
