package com.anemonesdk.general.storage

import com.anemonesdk.general.json.JSONMappable
import com.anemonesdk.general.json.JSONObject
import com.anemonesdk.general.json.JSONSerializable
import com.playtomic.foundation.extension.tryOrNull
import java.util.*

/**
 * Created by agarcia on 23/12/2016.
 */

interface IKeyValueStorage {

    fun getBool(name: String): Boolean?

    fun setBool(name: String, value: Boolean?)

    fun getString(name: String): String?

    fun setString(name: String, value: String?)

    fun getInt(name: String): Int?

    fun getDouble(name: String): Double?

    fun setDouble(name: String, value: Double?)

    fun setInt(name: String, value: Int?)

    fun getLong(name: String): Long?

    fun setLong(name: String, value: Long?)

    fun getData(name: String): ByteArray?

    fun setData(name: String, value: ByteArray?)
}

public fun IKeyValueStorage.getDate(name: String): Date? {
    val value = getLong(name = name) ?: return null
    return Date(value)
}

public fun IKeyValueStorage.setDate(name: String, value: Date?) {
    setLong(name = name, value = value?.time)
}

public fun <T : JSONMappable> IKeyValueStorage.getObject(name: String, clazz: Class<T>): T? {
    val jsonData = getData(name = name) ?: return null
    val json = tryOrNull { JSONObject(data = jsonData) } ?: return null
    val constructor = tryOrNull { clazz.getDeclaredConstructor(JSONObject::class.java) }
    return tryOrNull { constructor?.newInstance(json) }
}

public fun <T : JSONSerializable> IKeyValueStorage.setObject(name: String, value: T?) {
    setData(name = name, value = tryOrNull { value?.toJson()?.toData() })
}
