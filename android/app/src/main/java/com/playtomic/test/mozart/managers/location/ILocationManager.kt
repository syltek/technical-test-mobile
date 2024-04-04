package com.playtomic.mozart.managers.location

import com.playtomic.foundation.model.Coordinate
import com.playtomic.foundation.promise.Promise
import com.playtomic.mozart.managers.location.model.Location
import com.playtomic.mozart.managers.location.model.LocationServiceStatus

/**
 * Created by agarcia on 23/12/2016.
 */

interface ILocationManager {
    val defaultCoordinate: Coordinate

    val lastLocation: Location?

    val locationStatus: LocationServiceStatus

    fun findLocation(
        permissionRequest: LocationPermissionRequest,
        minAccuracy: Double = LocationManager.Defaults.minAccuracy,
        maxAge: Double = LocationManager.Defaults.maxAge,
        timeout: Double = LocationManager.Defaults.timeout
    ): Promise<Location>

    fun hasPermission(): Boolean
    fun requestLocationPermission()
}

val ILocationManager.bestKnownCoordinate: Coordinate
    get() = lastLocation?.coordinate ?: defaultCoordinate

fun ILocationManager.findOptimisticLocation(
    permissionRequest: LocationPermissionRequest,
    maxAge: Double = LocationManager.Defaults.maxAge,
    timeout: Double
) = findLocation(permissionRequest = permissionRequest, maxAge = maxAge, timeout = timeout)
    .fulfillOnError(promise = { error ->
        this.lastLocation?.let { Promise(value = it) } ?: Promise(error = error)
    })

enum class LocationPermissionRequest {
    never,
    always,
    default
}
