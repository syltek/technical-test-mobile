package com.anemonesdk.model.tenant

import com.anemonesdk.general.json.JSONMappable
import com.anemonesdk.general.json.JSONObject
import com.anemonesdk.model.config.SportId
import com.anemonesdk.model.generic.Duration
import org.json.JSONException

/**
 * Created by agarcia on 19/12/2017.
 */

class CancelationPolicy : JSONMappable {
    val duration: Duration?
    val sportId: SportId

    @Throws(JSONException::class)
    constructor(json: JSONObject) : super(json) {
        val duration = json.optJSONObject("duration")
        if (duration != null) {
            this.duration = Duration(json = duration)
        } else {
            this.duration = null
        }
        this.sportId = SportId(json.getAny("sport_id"))
    }

    override fun equals(other: Any?): Boolean =
        other is CancelationPolicy && other.duration == duration && other.sportId == sportId

    override fun hashCode(): Int = duration.hashCode()
}
