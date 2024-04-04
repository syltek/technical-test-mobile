package com.anemonesdk.model.generic

import com.anemonesdk.general.json.JSONMappable
import com.anemonesdk.general.json.JSONObject
import org.json.JSONException

/**
 * Created by manuelgonzalezvillegas on 14/3/17.
 */

class Message : JSONMappable {

    var message: String

    var status: String

    @Throws(JSONException::class)
    constructor(json: JSONObject) : super(json) {
        this.message = json.getString("localized_message")
        this.status = json.getString("status")
    }

    constructor(message: String, status: String) : super() {
        this.message = message
        this.status = status
    }
}
