package com.example.tube

import android.app.Application
import brigitte.viewmodel.CommandEventViewModel
import com.google.android.gms.location.LocationRequest
import com.patloew.rxlocation.RxLocation
import io.reactivex.disposables.CompositeDisposable
import org.slf4j.LoggerFactory
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2020-02-05 <p/>
 */

class MainViewModel @Inject constructor(
    app: Application
) : CommandEventViewModel(app) {
    companion object {
        private val logger = LoggerFactory.getLogger(MainViewModel::class.java)
    }
}