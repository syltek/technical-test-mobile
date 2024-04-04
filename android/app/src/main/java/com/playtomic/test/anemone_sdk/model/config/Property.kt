package com.anemonesdk.model.config

import com.anemonesdk.general.json.JSONMappable
import com.anemonesdk.general.json.JSONObject
import com.anemonesdk.model.generic.Id
import org.json.JSONException

/**
 * Created by agarcia on 23/12/2016.
 */
typealias PropertyId = Id

class Property : JSONMappable {
    companion object {
        val durationPropertyId = PropertyId("duration")
        val sizePropertyId = PropertyId("resource_size")
        val typePropertyId = PropertyId("resource_type")
        val featurePropertyId = PropertyId("resource_feature")
    }

    val id: PropertyId
    val name: String
    val options: List<PropertyOption>

    constructor(id: PropertyId, name: String, options: List<PropertyOption>) {
        this.id = id
        this.name = name
        this.options = options
    }

    @Throws(JSONException::class)
    constructor(json: JSONObject) : super(json) {
        id = PropertyId(json.getAny("property_id"))
        name = json.getString("name")
        options = json.getJSONArray("options").flatMap { f -> PropertyOption(f) }
    }

    fun optionWithId(id: PropertyOptionId): PropertyOption? {
        return options.filter { it.id == id }.firstOrNull()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        return (other as? Property)?.id == id
    }

    override fun hashCode(): Int = id.hashCode()
}
