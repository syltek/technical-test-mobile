package com.anemonesdk.model.tenant

import com.anemonesdk.general.exception.AnemoneException
import com.anemonesdk.general.json.JSONMappable
import com.anemonesdk.general.json.JSONObject
import com.anemonesdk.general.json.JSONSerializable
import com.anemonesdk.model.config.Property
import com.anemonesdk.model.config.PropertyId
import com.anemonesdk.model.config.PropertyOptionId
import com.anemonesdk.model.config.SportId
import com.anemonesdk.model.generic.Id
import com.playtomic.foundation.extension.tryOrNull
import org.json.JSONException

/**
 * Created by mgonzalez on 20/12/16.
 */

typealias TenantResourceId = Id
typealias DurationInMinutes = Int

class TenantResource : JSONMappable, JSONSerializable, Comparable<TenantResource> {

    val id: TenantResourceId
    lateinit var name: String // agarcia: name must be a lateinit to be used from the lazy property
    val sportId: SportId
    val properties: Map<PropertyId, PropertyOptionId>
    val priority: Int
    val number: Int? by lazy {
        Regex(pattern = "\\d+").find(this.name, 0)?.value?.toInt()
    }
    val allowedDurationIncrements: List<DurationInMinutes>

    @Throws(JSONException::class)
    constructor(json: JSONObject) : super(json) {
        this.id = TenantResourceId(json.getAny("resource_id"))
        this.name = json.getString("name")
        this.sportId = SportId(json.getAny("sport_id"))
        this.priority = json.optInt("reservation_priority") ?: 0
        val propertyValues = json.optJSONObject("properties")?.asMap<Any>()
            ?: throw AnemoneException.jsonInvalidFormat(key = "properties")
        val propertyMap = mutableMapOf<PropertyId, PropertyOptionId>()
        propertyValues.forEach { entry ->
            propertyMap[PropertyId(entry.key)] = PropertyOptionId(entry.value)
        }
        this.properties = propertyMap
        this.allowedDurationIncrements = tryOrNull { json.getJSONObject("booking_settings").getIntArray("allowed_duration_increments") } ?: emptyList()
    }

    constructor(
        id: TenantResourceId,
        name: String,
        sportId: SportId,
        properties: Map<PropertyId, PropertyOptionId>,
        priority: Int,
        allowedDurationIncrements: List<DurationInMinutes>
    ) : super() {
        this.id = id
        this.name = name
        this.sportId = sportId
        this.properties = properties
        this.priority = priority
        this.allowedDurationIncrements = allowedDurationIncrements
    }

    override fun toJson(): JSONObject {
        val json = JSONObject()
        json.setString("resource_id", id.description)
        json.setString("name", name)
        json.setString("sport_id", sportId.description)
        json.setInt("reservation_priority", priority)
        val propertiesJson = JSONObject()
        properties.forEach { (k, v) ->
            propertiesJson.setString(k.description, v.description)
        }
        json.setObject("properties", propertiesJson)
        val allowedDurationIncrementsObject = JSONObject()
        allowedDurationIncrementsObject.setIntArray("allowed_duration_increments", allowedDurationIncrements)
        json.setObject("booking_settings", allowedDurationIncrementsObject)
        return json
    }

    fun fulfillsFilters(filters: Map<PropertyId, List<PropertyOptionId>>?): Boolean {
        filters ?: return true
        for ((filterId, optionIds) in filters) {
            if (filterId != Property.durationPropertyId) {
                val resourceOptionId = this.properties[filterId]
                if (resourceOptionId == null || !optionIds.contains(resourceOptionId)) {
                    return false
                }
            }
        }
        return true
    }

    override fun equals(other: Any?) = (other as? TenantResource)?.id == id

    override fun hashCode() = id.hashCode()

    override fun compareTo(other: TenantResource): Int {
        if (this.priority != other.priority) {
            return this.priority.compareTo(other.priority)
        }
        val number1 = this.number ?: 0
        val number2 = other.number ?: 0
        if (number1 != number2) {
            return number1.compareTo(number2)
        }
        return this.name.compareTo(other.name)
    }
}
