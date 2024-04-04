package com.playtomic.foundation.model

import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Created by mgonzalez on 20/12/16.
 */

data class Coordinate(
    val lat: Double,
    val lon: Double
) : CustomStringConvertible {

    val latitude get() = lat
    val longitude get() = lon

    companion object

    fun distance(from: Coordinate, euclidian: Boolean): Double {
        return if (euclidian) { // mtenes: Used for calculating the plain distance between two points. More context: https://app.clickup.com/t/865cfkm50?comment=90080034677210
            sqrt((from.longitude - longitude).pow(2.0) + (from.latitude - latitude).pow(2.0)) * 100.0 * 1000.0 // 1º longitude ≈ 100km * 1000m
        } else {
            val location1 = android.location.Location("")
            location1.latitude = latitude
            location1.longitude = longitude

            val location2 = android.location.Location("")
            location2.latitude = from.latitude
            location2.longitude = from.longitude

            location1.distanceTo(location2).toDouble()
        }
    }

    fun formattedDescription(): String =
        String.format("(%.2f, %.2f)", latitude, longitude)

    override val description get() = "$latitude,$longitude"
}

fun Coordinate?.coordinateIsEquivalentTo(other: Coordinate?, toleranceInMeters: Double): Boolean =
    when {
        this == other -> true
        this == null || other == null -> false
        else -> this.distance(from = other, euclidian = false) <= toleranceInMeters
    }
