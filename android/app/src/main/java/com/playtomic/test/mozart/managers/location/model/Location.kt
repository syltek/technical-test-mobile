package com.playtomic.mozart.managers.location.model

import com.playtomic.foundation.model.Coordinate
import java.util.*

/**
 * Created by agarcia on 23/12/2016.
 */

data class Location(
    val timestamp: Date,
    val accuracy: Double?,
    val coordinate: Coordinate
) {
    constructor(location: android.location.Location) : this(
        Date(location.time),
        location.accuracy.toDouble(),
        Coordinate(location.latitude, location.longitude)
    )
}
