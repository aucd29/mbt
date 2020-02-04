package com.example.tube.ui

import android.os.Bundle
import androidx.fragment.app.FragmentManager
import brigitte.*
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.inject.Named

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2018. 12. 13. <p/>
 *
 */
class Navigator @Inject constructor(
    val manager: FragmentManager
) {
    companion object {
        private val logger = LoggerFactory.getLogger(Navigator::class.java)

//        const val CONTAINER          = R.id.rootContainer
//        const val FAVORITE_CONTAINER = R.id.favorite_container
    }

    fun mainFragment() {
        if (logger.isInfoEnabled) {
            logger.info("MAIN FRAGMENT")
        }

//        manager.show<MainFragment>(FragmentParams(CONTAINER, commit = FragmentCommit.NOW,
//            backStack = false))
    }
}
