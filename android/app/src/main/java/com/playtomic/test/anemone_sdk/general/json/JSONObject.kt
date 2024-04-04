@file:Suppress("UNCHECKED_CAST")

package com.anemonesdk.general.json

import com.playtomic.foundation.extension.Dates
import com.playtomic.foundation.extension.compactMap
import com.playtomic.foundation.extension.roundedValue
import com.playtomic.foundation.extension.toString
import com.playtomic.foundation.model.Time
import org.json.JSONException
import java.math.BigDecimal
import java.util.*

/**
 * Created by mgonzalez on 18/1/17.
 */

class JSONObject(internal val data: org.json.JSONObject = org.json.JSONObject()) {

    constructor(string: String) : this(data = org.json.JSONObject(string))

    constructor(data: ByteArray) : this(string = String(data))

    constructor(data: Map<String, Any>) : this() {
        data.forEach { (key, value) ->
            setAny(key, value)
        }
    }

    fun has(name: String) = !data.isNull(name)

    fun keys(): List<String> = data.keys().asSequence().toList()

    fun optAny(name: String): Any? =
        if (data.isNull(name)) null else data.opt(name)

    @Throws(JSONException::class)
    fun getAny(name: String) = optAny(name) ?: throw JSONException("Expected value for " + name)

    fun optInt(name: String): Int? {
        val obj = data.opt(name)
        if (obj is Int) {
            return obj
        }
        return null
    }

    @Throws(JSONException::class)
    fun getInt(name: String) = optInt(name) ?: throw JSONException("Expected int for " + name)

    fun optDouble(name: String): Double? {
        val obj = data.opt(name)
        if (obj is Double) {
            return obj
        } else if (obj is Number) {
            return obj.toDouble()
        }

        return null
    }

    @Throws(JSONException::class)
    fun getDouble(name: String) = optDouble(name)
        ?: throw JSONException("Expected double for " + name)

    fun optDecimal(name: String, decimals: Int): BigDecimal? {
        val obj = data.opt(name)
        return when (obj) {
            is BigDecimal -> obj
            is Double -> BigDecimal(obj).roundedValue(decimals = decimals).stripTrailingZeros()
            is Number -> BigDecimal(obj.toDouble()).roundedValue(decimals = decimals).stripTrailingZeros()
            else -> null
        }
    }

    @Throws(JSONException::class)
    fun getDecimal(name: String, decimals: Int) = optDecimal(name, decimals = decimals)
        ?: throw JSONException("Expected double for " + name)

    fun optBoolean(name: String): Boolean? = data.opt(name) as? Boolean

    @Throws(JSONException::class)
    fun getBoolean(name: String): Boolean = optBoolean(name)
        ?: throw JSONException("Expected double for " + name)

    fun optString(name: String) = data.opt(name) as? String

    @Throws(JSONException::class)
    fun getString(name: String) = optString(name)
        ?: throw JSONException("Expected String for " + name)

    fun optDate(name: String): Date? {
        val obj = data.opt(name) as? String ?: return null
        return Dates.from(string = obj, format = Dates.defaultFormat, timeZone = Dates.UTC_TIME_ZONE)
    }

    @Throws(JSONException::class)
    fun getDate(name: String) = optDate(name) ?: throw JSONException("Expected Date for " + name)

    fun optTime(key: String): Time? {
        val value = data[key] as? String ?: return null
        val components = value.split(":").compactMap { it.toIntOrNull() }
        if (components.size != 3) {
            return null
        }
        return Time.of(hour = components[0], minute = components[1], second = components[2])
    }

    fun getTime(key: String): Time = optTime(key) ?: throw JSONException("Expected Time for " + key)

    fun optJSONObject(name: String): JSONObject? {
        if (data.isNull(name)) {
            return null
        }
        val obj = data.opt(name)
        if (obj is org.json.JSONObject) {
            return JSONObject(obj)
        }

        return null
    }

    @Throws(JSONException::class)
    fun getJSONObject(name: String) = optJSONObject(name)
        ?: throw JSONException("Expected JSONObject for " + name)

    fun optJSONArray(name: String): JSONArray? {
        val obj = data.opt(name)
        if (obj is org.json.JSONArray) {
            return JSONArray(obj)
        }

        return null
    }

    @Throws(JSONException::class)
    fun getJSONArray(name: String) = optJSONArray(name)
        ?: throw JSONException("Expected JSONArray for " + name)

    fun optStringArray(name: String): List<String>? {
        try {
            val array = getJSONArray(name)
            return array.map { item -> item as String }
        } catch (je: JSONException) {
            return null
        }
    }

    @Throws(JSONException::class)
    fun getStringArray(name: String) = optStringArray(name)
        ?: throw JSONException("Expected String Array for " + name)

    fun optIntArray(name: String): List<Int>? {
        try {
            val array = getJSONArray(name)
            return array.map { item -> item as Int }
        } catch (je: JSONException) {
            return null
        }
    }

    @Throws(JSONException::class)
    fun getIntArray(name: String) = optIntArray(name)
        ?: throw JSONException("Expected Int Array for " + name)

    fun optAnyArray(name: String): List<Any>? {
        try {
            val array = getJSONArray(name)
            return array.map { it }
        } catch (je: JSONException) {
            return null
        }
    }

    @Throws(JSONException::class)
    fun getAnyArray(name: String) = optAnyArray(name)
        ?: throw JSONException("Expected Any Array for " + name)

    @Throws(JSONException::class)
    fun <T> asMap(): Map<String, T> {
        val map = mutableMapOf<String, T>()
        val iterator = data.keys()
        while (iterator.hasNext()) {
            val key = iterator.next()
            if (data.isNull(key)) { continue }
            val obj = data.get(key)
            map[key] = when (obj) {
                is org.json.JSONArray -> JSONArray(obj).asList<Any>()
                is org.json.JSONObject -> JSONObject(obj).asMap<Any>()
                else -> obj
            } as T
        }

        return map
    }

    fun setAny(key: String, value: Any?) {
        when (value) {
            is Int -> setInt(key, value)
            is Boolean -> setBoolean(key, value)
            is Date -> setDate(key, value)
            is BigDecimal -> setDecimal(key, value)
            is Double -> setDouble(key, value)
            is String -> setString(key, value)
            is Time -> setTime(key, value)
            is List<*> -> setAnyArray(key, value as List<Any>)
            is Map<*, *> -> setObject(key, JSONObject(value as Map<String, Any>))
            else -> data.put(key, value)
        }
    }

    @Throws(JSONException::class)
    fun setInt(key: String, value: Int?) {
        data.put(key, value)
    }

    @Throws(JSONException::class)
    fun setDouble(key: String, value: Double?) {
        data.put(key, value)
    }

    @Throws(JSONException::class)
    fun setDecimal(key: String, value: BigDecimal?) {
        data.put(key, value)
    }

    @Throws(JSONException::class)
    fun setString(key: String, value: String?) {
        data.put(key, value)
    }

    @Throws(JSONException::class)
    fun setBoolean(key: String, value: Boolean?) {
        data.put(key, value)
    }

    @Throws(JSONException::class)
    fun setObject(key: String, value: JSONObject?) {
        if (value != null) {
            data.put(key, value.data)
        } else {
            data.remove(key)
        }
    }

    @Throws(JSONException::class)
    fun setAnyArray(key: String, value: List<Any>?) {
        if (value != null) {
            val array = JSONArray()
            value.forEach { array.addAny(it) }
            data.put(key, array.data)
        } else {
            data.remove(key)
        }
    }

    @Throws(JSONException::class)
    fun setArray(key: String, value: List<JSONObject>?) {
        if (value != null) {
            val array = JSONArray()
            array.addAll(value)
            data.put(key, array.data)
        } else {
            data.remove(key)
        }
    }

    @Throws(JSONException::class)
    fun setJSONArray(key: String, value: JSONArray?) {
        if (value != null) {
            data.put(key, value.data)
        } else {
            data.remove(key)
        }
    }

    @Throws(JSONException::class)
    fun setStringArray(key: String, value: List<String>?) {
        if (value != null) {
            val array = JSONArray()
            array.addAllString(value)
            data.put(key, array.data)
        } else {
            data.remove(key)
        }
    }

    @Throws(JSONException::class)
    fun setIntArray(key: String, value: List<Int>?) {
        if (value != null) {
            val array = JSONArray()
            array.addAllInt(value)
            data.put(key, array.data)
        } else {
            data.remove(key)
        }
    }

    @Throws(JSONException::class)
    fun setDate(key: String, value: Date?) {
        if (value == null) {
            data.remove(key)
            return
        }
        data.put(key, value.toString(format = Dates.defaultFormat, timeZone = Dates.UTC_TIME_ZONE))
    }

    fun setTime(key: String, value: Time?) {
        if (value == null) {
            data.remove(key)
            return
        }
        data.put(key, value.description)
    }

    fun toData() = toString().toByteArray()

    override fun toString() = data.toString().replace("\\/", "/")
}
