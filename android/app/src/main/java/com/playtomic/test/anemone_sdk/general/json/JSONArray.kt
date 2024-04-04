@file:Suppress("UNCHECKED_CAST")

package com.anemonesdk.general.json

import org.json.JSONException
import java.math.BigDecimal
import java.util.*

/**
 * Created by mgonzalez on 18/1/17.
 */

class JSONArray {
    constructor(string: String) : this(array = org.json.JSONArray(string))
    constructor(data: ByteArray) : this(string = String(data))
    constructor(objects: List<JSONObject>) : this(org.json.JSONArray(objects.map { it.data }))
    constructor(array: org.json.JSONArray = org.json.JSONArray()) {
        val objects = mutableListOf<Any>()
        for (index in 0 until array.length()) {
            if (!array.isNull(index)) {
                objects.add(array[index])
            }
        }
        data = org.json.JSONArray(objects)
    }

    internal val data: org.json.JSONArray

    fun length() = data.length()

    @Throws(JSONException::class)
    fun getJSONObject(index: Int): JSONObject = JSONObject(data.getJSONObject(index))

    @Throws(JSONException::class)
    fun getString(index: Int) = data.get(index) as? String
        ?: throw JSONException("Expected String for firstIndex " + index)

    @Throws(JSONException::class)
    fun get(index: Int): Any = data.get(index)

    @Throws(JSONException::class)
    fun <U> flatMap(mapCallback: (JSONObject) -> U?): List<U> {
        val list = ArrayList<U>()
        for (i in 0 until data.length()) {
            val newObject = mapCallback(getJSONObject(i))
            if (newObject != null) {
                list.add(newObject)
            }
        }
        return list
    }

    @Throws(JSONException::class)
    fun forEach(mapCallback: (JSONObject) -> Unit) {
        for (i in 0 until data.length()) {
            mapCallback(getJSONObject(i))
        }
    }

    @Throws(JSONException::class)
    fun <U> map(mapCallback: (Any) -> U): List<U> {
        val list = ArrayList<U>()
        for (i in 0..length() - 1) {
            list.add(mapCallback(data.get(i)))
        }
        return list
    }

    @Throws(JSONException::class)
    fun <T> asList(): List<T> {
        val list = mutableListOf<T>()
        for (i in 0 until data.length()) {
            val obj = data.get(i)
            when (obj) {
                is org.json.JSONObject -> list.add(JSONObject(data = obj).asMap<Any>() as T)
                is org.json.JSONArray -> list.add(JSONArray(array = obj).asList<Any>() as T)
                else -> list.add(obj as T)
            }
        }
        return list
    }

    @Throws(JSONException::class)
    fun asJSONList(): List<JSONObject> {
        val list = mutableListOf<JSONObject>()
        for (i in 0 until data.length()) {
            list.add(getJSONObject(i))
        }
        return list
    }

    fun add(`object`: JSONObject) {
        data.put(`object`.data)
    }

    fun addAll(objects: List<JSONObject>) {
        objects.forEach { data.put(it.data) }
    }

    fun addAllString(objects: List<String>) {
        objects.forEach { data.put(it) }
    }

    fun addAllInt(objects: List<Int>) {
        objects.forEach { data.put(it) }
    }

    fun addInt(value: Int) {
        data.put(value)
    }

    fun addString(value: String) {
        data.put(value)
    }

    fun addDouble(value: Double) {
        data.put(value)
    }

    fun addDecimal(value: BigDecimal) {
        data.put(value)
    }

    fun addBoolean(value: Boolean) {
        data.put(value)
    }

    fun addAny(value: Any) {
        when (value) {
            is Int -> addInt(value)
            is Boolean -> addBoolean(value)
            is BigDecimal -> addDecimal(value)
            is Double -> addDouble(value)
            is String -> addString(value)
            is JSONObject -> add(value)
            is Map<*, *> -> add(JSONObject(value as Map<String, Any>))
            else -> data.put(value)
        }
    }

    fun toData() = toString().toByteArray()

    override fun toString() = data.toString()
}
