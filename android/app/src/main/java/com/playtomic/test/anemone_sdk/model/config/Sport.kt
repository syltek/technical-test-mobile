package com.anemonesdk.model.config

import com.anemonesdk.general.json.JSONMappable
import com.anemonesdk.general.json.JSONObject
import com.anemonesdk.model.generic.Id
import org.json.JSONException

/**
 * Created by agarcia on 23/12/2016.
 */

open class SportId : Id {

    @Throws(JSONException::class)
    constructor(value: Any) :
        super(
            value.toString().let { stringValue ->
                if (stringValue == "1") "PADEL"
                else if (stringValue == "2") "TENNIS"
                else stringValue
            }
        )

    companion object {
        val PADEL = SportId("PADEL")
        val TENNIS = SportId("TENNIS")
        val PADBOL = SportId("PADBOL")
        val SQUASH = SportId("SQUASH")
        val BADMINTON = SportId("BADMINTON")
        val FOOTBALL7 = SportId("FOOTBALL7")
        val FOOTBALL11 = SportId("FOOTBALL11")
        val FUTSAL = SportId("FUTSAL")
        val FOOTBALL_OTHERS = SportId("FOOTBALL_OTHERS")
        val PICKLEBALL = SportId("PICKLEBALL")
        val BEACH_TENNIS = SportId("BEACH_TENNIS")
        val BEACH_VOLLEY = SportId("BEACH_VOLLEY")
        val BASKETBALL = SportId("BASKETBALL")
        val VOLLEYBALL = SportId("VOLLEYBALL")
        val TABLE_TENNIS = SportId("TABLE_TENNIS")
        val CRICKET = SportId("CRICKET")
    }
}

class Sport : JSONMappable {

    val id: SportId
    val name: String
    val isRacket: Boolean
    val properties: List<Property>
    val warnings: List<SportWarning>

    constructor(id: SportId, name: String, isRacket: Boolean, properties: List<Property>, warnings: List<SportWarning>) {
        this.id = id
        this.name = name
        this.isRacket = isRacket
        this.properties = properties
        this.warnings = warnings
    }

    @Throws(JSONException::class)
    constructor(json: JSONObject) : super(json) {
        id = SportId(json.getAny("sport_id"))
        name = json.getString("name")
        isRacket = json.getBoolean("is_racket")
        properties = json.getJSONArray("properties").flatMap { f ->
            try {
                Property(f)
            } catch (t: Exception) {
                null
            }
        }
        warnings = json.optJSONArray("warnings")?.flatMap {
            try {
                SportWarning(it)
            } catch (t: Exception) {
                null
            }
        } ?: listOf()
    }

    fun propertyWithId(id: PropertyId) =
        properties.firstOrNull { it.id == id }

    fun warningFor(propertyId: PropertyId, optionId: PropertyOptionId) =
        warnings.firstOrNull { it.propertyId == propertyId && it.optionId == optionId }

    fun filterProperties(filterIds: List<PropertyId>): List<Property> = properties.filter { filterIds.map { id -> id.value }.contains(it.id.value) }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        return (other as? Sport)?.id == id
    }

    override fun hashCode(): Int = id.hashCode()
}
