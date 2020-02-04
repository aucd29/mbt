package com.example.tube.di.component

import com.example.tube.MainApp
import com.example.tube.di.module.*
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2018. 12. 5. <p/>
 *
 * https://medium.com/@iammert/new-android-injector-with-dagger-2-part-1-8baa60152abe
 * https://github.com/googlesamples/android-architecture-components/tree/master/GithubBrowserSample/app/src/main/java/com/android/example/github
 */
@Singleton
@Component(modules = [
    AndroidSupportInjectionModule::class,
    AppModule::class,
    ActivityModule::class,
    UtilModule::class,
    NetworkModule::class
])
interface AppComponent: AndroidInjector<MainApp> {
    @Component.Factory
    interface Factory : AndroidInjector.Factory<MainApp>
}