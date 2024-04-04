package com.anemonesdk.general.extension

import com.anemonesdk.general.json.JSONObject
import com.playtomic.foundation.extension.tryOrNull
import com.playtomic.foundation.model.TimeZoneDateRange
import java.util.*

fun TimeZoneDateRange.Companion.from(json: JSONObject): TimeZoneDateRange =
    TimeZoneDateRange(
        from = json.getDate("from"),
        to = json.getDate("to"),
        timeZone = tryOrNull { TimeZone.getTimeZone(json.optString("timezone") ?: "") }
            ?: TimeZone.getDefault()
    )

fun TimeZoneDateRange.toJson(): JSONObject {
    val json = JSONObject()
    json.setDate("from", from)
    json.setDate("to", to)
    json.setString("timezone", timeZone.id)
    return json
}
