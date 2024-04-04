package com.anemonesdk.model.config

import com.anemonesdk.general.json.JSONMappable
import com.anemonesdk.general.json.JSONObject
import com.anemonesdk.model.generic.Id
import org.json.JSONException

/**
 * Created by agarcia on 23/12/2016.
 */

typealias PropertyOptionId = Id

class PropertyOption : JSONMappable {
    companion object {
        val singleSizePropertyOption = PropertyOptionId("single")
        val doubleSizePropertyOption = PropertyOptionId("double")
        val miniSizePropertyOption = PropertyOptionId("mini")
    }

    val id: PropertyOptionId
    val name: String
    val filterable: Boolean

    constructor(id: PropertyOptionId, name: String, filterable: Boolean = true) {
        this.id = id
        this.name = name
        this.filterable = filterable
    }

    @Throws(JSONException::class)
    constructor(json: JSONObject) : super(json) {
        id = PropertyOptionId(json.getAny("option_id"))
        name = json.getString("name")
        filterable = json.optBoolean("filterable") ?: true
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        return (other as? PropertyOption)?.id == id
    }

    override fun hashCode(): Int = id.hashCode()
}
