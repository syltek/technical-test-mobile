package com.anemonesdk.model.generic

import com.anemonesdk.general.exception.AnemoneException
import com.anemonesdk.general.json.JSONMappable
import com.anemonesdk.general.json.JSONObject
import com.anemonesdk.general.json.JSONSerializable
import org.json.JSONException

/**
 * Created by agarcia on 19/12/2017.
 */

class Duration : JSONMappable, JSONSerializable {
    val amount: Int
    val unit: Unit

    enum class Unit(val rawValue: String) {
        seconds("SECONDS"), minutes("MINUTES"), hours("HOURS"), days("DAYS"), weeks("WEEKS"), months("MONTHS"), years("YEARS");

        companion object {
            fun fromRawValue(rawValue: String): Unit? = entries.firstOrNull { it.rawValue == rawValue.uppercase() }
        }
    }

    constructor(amount: Int, unit: Unit) : super() {
        this.amount = amount
        this.unit = unit
    }

    @Throws(JSONException::class, AnemoneException::class)
    constructor(json: JSONObject) : super(json) {
        this.amount = json.getInt("amount")
        val duration = Unit.fromRawValue(json.getString("unit"))
            ?: throw AnemoneException.jsonInvalidFormat(key = "unit")
        this.unit = duration
    }

    override fun toJson(): JSONObject {
        val json = JSONObject()
        json.setInt("amount", amount)
        json.setString("unit", unit.rawValue)
        return json
    }

    override fun equals(other: Any?): Boolean =
        (other is Duration) && other.unit == unit && other.amount == amount

    override fun hashCode(): Int = amount.hashCode()
}

fun Duration.toUnit(unit: Duration.Unit): Duration {
    val selfMultiplier = this.unit.secondsMultiplier
    val unitMultiplier = unit.secondsMultiplier
    return Duration(amount = (this.amount.toDouble() * selfMultiplier / unitMultiplier).toInt(), unit = unit)
}

fun Duration.toAccurateUnit(): Duration {
    var duration = this
    val selfMultiplier = this.unit.secondsMultiplier

    val seconds = amount.toDouble() * selfMultiplier / Duration.Unit.seconds.secondsMultiplier
    if (seconds == seconds.toInt().toDouble() && seconds >= 60) {
        duration = Duration(amount = seconds.toInt(), unit = Duration.Unit.seconds)
    }

    val minutes = amount.toDouble() * selfMultiplier / Duration.Unit.minutes.secondsMultiplier
    if (minutes == minutes.toInt().toDouble() && minutes > 1) {
        duration = Duration(amount = minutes.toInt(), unit = Duration.Unit.minutes)
    }

    val hours = amount.toDouble() * selfMultiplier / Duration.Unit.hours.secondsMultiplier
    if (hours == hours.toInt().toDouble() && hours >= 2) {
        duration = Duration(amount = hours.toInt(), unit = Duration.Unit.hours)
    }

    val days = amount.toDouble() * selfMultiplier / Duration.Unit.days.secondsMultiplier
    if (days == days.toInt().toDouble() && days > 2) {
        duration = Duration(amount = days.toInt(), unit = Duration.Unit.days)
    }

    return duration
}

val Duration.Unit.secondsMultiplier: Double
    get() = when (this) {
        Duration.Unit.seconds -> 1.0
        Duration.Unit.minutes -> 60.0
        Duration.Unit.hours -> 3600.0
        Duration.Unit.days -> 3600.0 * 24.0
        Duration.Unit.weeks -> 3600.0 * 24.0 * 7.0
        Duration.Unit.months -> 3600.0 * 24.0 * 31.0
        Duration.Unit.years -> 3600.0 * 24.0 * 365.0
    }
