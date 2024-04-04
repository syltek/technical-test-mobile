package com.anemonesdk.general.json

import org.json.JSONException

/**
 * Created by mgonzalez on 21/12/16.
 */

abstract class JSONMappable {

    @Suppress("UNUSED_PARAMETER")
    @Throws(JSONException::class)
    constructor(json: JSONObject)

    constructor()
}
