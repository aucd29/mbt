@file:Suppress("NOTHING_TO_INLINE", "unused")
package brigitte

import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build
import android.os.Looper
import androidx.core.location.LocationManagerCompat

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019. 1. 22. <p/>
 */

@SuppressLint("MissingPermission")
inline fun Context.isNetworkConntected(): Boolean {
    systemService<ConnectivityManager>()?.apply {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            for (network in allNetworks) {
                if (getNetworkInfo(network).state == NetworkInfo.State.CONNECTED) {
                    return true
                }
            }
        } else {
            for (network in allNetworkInfo) {
                if (network.state == NetworkInfo.State.CONNECTED) {
                    return true
                }
            }
        }
    }

    return false
}

inline fun Context.isLocationEnabled(): Boolean =
    systemService<LocationManager>()?.run {
        LocationManagerCompat.isLocationEnabled(this)
    } ?: false