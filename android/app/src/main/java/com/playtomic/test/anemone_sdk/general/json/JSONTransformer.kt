package com.anemonesdk.general.json

import com.anemonesdk.general.exception.AnemoneException
import com.playtomic.foundation.logger.Log
import com.playtomic.foundation.promise.Promise
import java.lang.reflect.Constructor

/**
 * Created by mgonzalez on 21/12/16.
 */

class JSONTransformer<T : JSONMappable>(val clazz: Class<T>) {
    var constructor: Constructor<T>? = null

    init {
        try {
            constructor = clazz.getDeclaredConstructor(JSONObject::class.java)
        } catch (t: Exception) {
            Log.e("JSONTransformer", "Object $clazz does not implement JSONMappable")
        }
    }

    var rootKey: String? = null

    fun transformObject(data: ByteArray): T? {
        try {
            val stringObject = String(data)
            val orgJson = org.json.JSONObject(stringObject)
            var json = JSONObject(orgJson)

            rootKey?.let { json = json.getJSONObject(it) }

            return instantiate(json)
        } catch (je: Exception) {
            Log.w("JSONTransformer", "Can not transform object of type $clazz because $je")
            return null
        }
    }

    fun transformArray(data: ByteArray): List<T>? {
        return try {
            val stringArray = String(data)
            val jsonArray: JSONArray = if (rootKey != null) {
                val orgJson = org.json.JSONObject(stringArray)
                JSONArray(orgJson.getJSONArray(rootKey ?: ""))
            } else {
                val array = org.json.JSONArray(stringArray)
                JSONArray(array)
            }
            jsonArray.flatMap(this::instantiate)
        } catch (je: Exception) {
            Log.w("JSONTransformer", "Can not transform array of type $clazz because $je")
            null
        }
    }

    fun mapObject(data: ByteArray): Promise<T> =
        Promise(executeInBackground = true) { fulfill, reject ->
            val `object` = transformObject(data)
            if (`object` != null) {
                fulfill.invoke(`object`)
            } else {
                reject.invoke(AnemoneException.notMappable)
            }
        }

    fun mapArray(data: ByteArray): Promise<List<T>> =
        Promise(executeInBackground = true) { fulfill, reject ->
            val objects = transformArray(data)
            if (objects != null) {
                fulfill.invoke(objects)
            } else {
                reject.invoke(AnemoneException.notMappable)
            }
        }

    private fun instantiate(json: JSONObject): T? =
        try {
            constructor?.newInstance(json)
        } catch (e: Exception) {
            Log.w("JSONTransformer", "Can not instantiate object of type $clazz because ${e.cause}")
            null
        }
}
