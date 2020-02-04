package com.example.tube.di.module

import brigitte.di.dagger.module.RxModule
import brigitte.di.dagger.module.ViewModelFactoryModule
import com.example.tube.di.module.libs.RxLocationModule
import dagger.Module

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2018. 12. 5. <p/>
 */

@Module(includes = [
//    CalligraphyModule::class,
//    ChipModule::class,
    RxLocationModule::class,
    RxModule::class,
    DbModule::class,
    ViewModelModule::class,
    ViewModelFactoryModule::class
//    KakaoModule::class
])
class UtilModule