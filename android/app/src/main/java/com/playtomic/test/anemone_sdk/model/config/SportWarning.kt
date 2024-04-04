package com.anemonesdk.model.config

import com.anemonesdk.general.json.JSONMappable
import com.anemonesdk.general.json.JSONObject
import org.json.JSONException

/**
 * Created by agarcia on 27/12/2017.
 */

class SportWarning : JSONMappable {
    val propertyId: PropertyId
    val optionId: PropertyOptionId
    val message: String

    constructor(propertyId: PropertyId, optionId: PropertyOptionId, message: String) {
        this.propertyId = propertyId
        this.optionId = optionId
        this.message = message
    }

    @Throws(JSONException::class)
    constructor(json: JSONObject) : super(json) {
        this.propertyId = PropertyId(json.getAny("property_id"))
        this.optionId = PropertyOptionId(json.getAny("option_id"))
        this.message = json.getString("message")
    }
}
