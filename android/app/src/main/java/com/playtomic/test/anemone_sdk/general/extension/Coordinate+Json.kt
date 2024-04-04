package com.anemonesdk.general.extension

import com.anemonesdk.general.json.JSONObject
import com.playtomic.foundation.model.Coordinate

fun Coordinate.Companion.from(json: JSONObject): Coordinate =
    Coordinate(
        lat = json.getDouble("lat"),
        lon = json.getDouble("lon")
    )

fun Coordinate.toJson(): JSONObject {
    val json = JSONObject()
    json.setDouble("lat", latitude)
    json.setDouble("lon", longitude)
    return json
}
