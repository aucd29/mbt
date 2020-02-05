package com.example.tube.di.module

import com.example.tube.ui.map.MapFragment
import dagger.Module

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2018. 12. 5. <p/>
 */

@Module(includes = [
    MapFragment.Module::class
])
abstract class FragmentModule