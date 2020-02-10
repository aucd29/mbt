package com.example.tube.di.module

import brigitte.di.dagger.module.RxModule
import brigitte.di.dagger.module.ViewModelFactoryModule
import com.example.tube.di.module.libs.RxLocationModule
import dagger.Module

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2018. 12. 5. <p/>
 */

@Module(includes = [
    RxLocationModule::class,
    RxModule::class,
    ViewModelModule::class,
    ViewModelFactoryModule::class
])
class UtilModule