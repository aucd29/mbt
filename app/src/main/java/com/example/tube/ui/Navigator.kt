package com.example.tube.ui

import androidx.fragment.app.FragmentManager
import brigitte.FragmentAnim
import brigitte.FragmentCommit
import brigitte.FragmentParams
import brigitte.show
import com.example.tube.R
import com.example.tube.ui.map.MapFragment
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.inject.Named

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2020. 2. 4. <p/>
 *
 */
class Navigator @Inject constructor(
    @param:Named("activityFragmentManager") val manager: FragmentManager
) {
    companion object {
        private val logger = LoggerFactory.getLogger(Navigator::class.java)

        const val CONTAINER = R.id.root_container
    }

    fun mapFragment() {
        if (logger.isInfoEnabled) {
            logger.info("MapFragment")
        }

        manager.show<MapFragment>(FragmentParams(CONTAINER, commit = FragmentCommit.NOW,
            backStack = false, anim = FragmentAnim.RIGHT))
    }
}
