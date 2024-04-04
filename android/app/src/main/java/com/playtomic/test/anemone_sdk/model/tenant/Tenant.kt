package com.anemonesdk.model.tenant

import com.anemonesdk.general.json.JSONMappable
import com.anemonesdk.general.json.JSONObject
import com.anemonesdk.model.config.PropertyId
import com.anemonesdk.model.config.PropertyOptionId
import com.anemonesdk.model.config.SportId
import com.anemonesdk.model.generic.Address
import com.anemonesdk.model.generic.Id
import com.playtomic.foundation.extension.tryOrNull
import com.playtomic.foundation.model.Time
import com.playtomic.foundation.model.TimeRange
import org.json.JSONException

/**
 * Created by mgonzalez on 20/12/16.
 */

open class TenantId(value: Any) : Id(value = value)

class Tenant : JSONMappable {
    val id: TenantId
    val name: String
    val description: String?
    val address: Address
    val images: List<String>
    val resources: List<TenantResource>
    val sportIds: List<SportId>
    val type: TenantType?
    val bookingType: BookingType
    val cancelationPolicies: List<CancelationPolicy>?
    val properties: Map<String, String>?
    val openingHours: Map<String, TimeRange>?

    constructor(json: JSONObject) : super(json) {
        id = TenantId(json.getAny("tenant_id"))
        name = json.optString("tenant_name") ?: json.getString("name")
        address = Address(json.getJSONObject("address"))
        images = json.getStringArray("images")
        description = json.optString("description")
        sportIds = json.getAnyArray("sport_ids").map { SportId(it) }
        type = json.optString("tenant_type")?.let { TenantType.fromRawValue(it) }
        resources = json.optJSONArray("resources")?.flatMap { obj -> tryOrNull { TenantResource(obj) } } ?: listOf()
        val bookingTypeStr = json.getString("booking_type")
        this.bookingType = BookingType.fromRawValue(bookingTypeStr)
            ?: throw JSONException("Invalid booking type $bookingTypeStr")
        cancelationPolicies = tryOrNull { json.getJSONArray("cancelation_policies").flatMap { CancelationPolicy(json = it) } }
        properties = (tryOrNull { json.getJSONObject("properties") })?.asMap()
        val openingJson = json.optJSONObject("opening_hours")
        if (openingJson != null) {
            val hours = mutableMapOf<String, TimeRange>()
            openingJson.keys().forEach { day ->
                val from = tryOrNull { Time(openingJson.getJSONObject(day).getString("opening_time")) }
                val to = tryOrNull { Time(openingJson.getJSONObject(day).getString("closing_time")) }
                if (from != null && to != null) {
                    hours[day] = TimeRange(from = from, to = to)
                }
            }
            this.openingHours = hours
        } else {
            this.openingHours = null
        }
    }

    constructor(
        id: TenantId,
        name: String,
        description: String,
        address: Address,
        images: List<String>,
        resources: List<TenantResource>,
        sportIds: List<SportId>,
        type: TenantType,
        bookingType: BookingType,
        cancelationPolicies: List<CancelationPolicy>?,
        properties: Map<String, String>?,
        openingHours: Map<String, TimeRange>?
    ) {
        this.id = id
        this.name = name
        this.description = description
        this.address = address
        this.images = images
        this.resources = resources
        this.sportIds = sportIds
        this.type = type
        this.bookingType = bookingType
        this.cancelationPolicies = cancelationPolicies
        this.properties = properties
        this.openingHours = openingHours
    }

    fun fulfillsFilters(filters: Map<PropertyId, List<PropertyOptionId>>?): Boolean {
        if (filters == null) {
            return true
        }

        for (resource in resources) {
            if (resource.fulfillsFilters(filters)) {
                return true
            }
        }
        return false
    }

    fun resources(sportId: SportId) = resources.filter { it.sportId == sportId }

    override fun equals(other: Any?) =
        (other is Tenant) && other.id == id

    override fun hashCode(): Int = id.hashCode()

}
