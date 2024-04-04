package com.playtomic.mozart.managers.location.model

import com.google.android.gms.location.LocationRequest
import java.util.Date
import java.util.TimerTask

/**
 * Created by agarcia on 20/01/2017.
 */

class LocationServiceRequest(
    private val minAccuracy: Double?,
    private val maxAge: Double,
    var timeout: Double,
    val fulfill: (Location) -> Unit,
    val reject: (Exception) -> Unit
) {

    var timer: TimerTask? = null

    fun isLocationValid(location: Location): Boolean {

        // Check the accuracy
        if (minAccuracy != null) {
            val locationAccuracy = location.accuracy ?: return false
            if (minAccuracy < locationAccuracy) {
                return false
            }
        }

        // Check the age
        if (maxAge > 0 && (Date().time - location.timestamp.time) > (maxAge * 1000)) {
            return false
        }

        return true
    }

    val locationRequest: LocationRequest
        get() = LocationRequest.Builder(1000)
            .setWaitForAccurateLocation(false)
            .setDurationMillis((timeout * 1000).toLong())
            .build()
}
