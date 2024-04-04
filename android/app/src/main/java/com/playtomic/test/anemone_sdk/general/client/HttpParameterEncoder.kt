@file:Suppress("UNCHECKED_CAST")

package com.anemonesdk.general.client

import android.util.Base64
import android.util.Log
import com.anemonesdk.general.exception.AnemoneException
import com.anemonesdk.general.json.JSONArray
import com.anemonesdk.general.json.JSONObject
import com.anemonesdk.general.json.JSONSerializable
import com.playtomic.foundation.extension.Dates
import com.playtomic.foundation.extension.compactMap
import com.playtomic.foundation.extension.toString
import com.playtomic.foundation.extension.tryOrNull
import com.playtomic.foundation.model.CustomStringConvertible
import org.json.JSONException
import java.io.UnsupportedEncodingException
import java.math.BigDecimal
import java.net.URLEncoder
import java.util.*

/**
 * Created by manuelgonzalezvillegas on 9/10/17.
 */

interface IHttpParameterEncoder {
    fun contentType(request: HttpRequest?): String

    @Throws(JSONException::class)
    fun stringEncode(params: Map<String, Any?>): String

    @Throws(JSONException::class)
    fun dataEncode(params: Any): ByteArray
}

class HttpUrlParameterEncoder : IHttpParameterEncoder {

    override fun contentType(request: HttpRequest?): String {
        return "application/x-www-form-urlencoded"
    }

    @Throws(JSONException::class)
    override fun stringEncode(params: Map<String, Any?>): String =
        urlEncoded(params)

    @Throws(JSONException::class)
    override fun dataEncode(params: Any): ByteArray =
        try {
            urlEncoded(params as Map<String, Any?>).toByteArray(Charsets.UTF_8)
        } catch (t: Exception) {
            val flatParams = mutableMapOf<String, Any?>()
            (params as List<Map<String, Any?>>).forEach {
                flatParams.putAll(it)
            }
            urlEncoded(flatParams).toByteArray(Charsets.UTF_8)
        }

    @Throws(JSONException::class)
    fun urlEncoded(params: Map<String, Any?>): String {
        val encodedParameters = params.keys.sorted().map { key ->
            val value = params[key] ?: return@map null
            val encoded = urlEncoded(value = value)
            "$key=$encoded"
        }.compactMap { it }

        return encodedParameters.joinToString(separator = "&")
    }

    @Throws(JSONException::class)
    private fun urlEncoded(value: Any?): String {
        if (value is List<*>) {
            return value.map { urlEncoded(value = it) }.joinToString(separator = ",")
        }

        if (value is Number || value is Boolean || value is String) {
            try {
                return URLEncoder.encode(value.toString(), "utf-8").replace("+", "%20")
            } catch (ex: UnsupportedEncodingException) {
                throw JSONException(ex.message)
            }
        }

        if (value is Date) {
            return value.toString(format = Dates.defaultFormat, timeZone = Dates.UTC_TIME_ZONE)
        }

        if (value is ByteArray) {
            return urlEncoded(value = Base64.encodeToString(value, Base64.DEFAULT))
        }

        if (value is CustomStringConvertible) {
            return urlEncoded(value = value.description)
        }

        throw JSONException("Invalid format")
    }
}

class HttpJsonParameterEncoder : IHttpParameterEncoder {

    override fun contentType(request: HttpRequest?): String {
        return "application/json"
    }

    override fun stringEncode(params: Map<String, Any?>): String =
        String(dataEncode(params), Charsets.UTF_8)

    override fun dataEncode(params: Any): ByteArray =
        try {
            jsonEncoded(params as Map<String, Any?>).toData()
        } catch (t: Exception) {
            jsonEncoded(params as List<Any>).toData()
        }

    @Throws(JSONException::class)
    fun jsonEncoded(params: Map<String, Any?>?): JSONObject {
        val json = JSONObject()
        params?.forEach { entry ->
            val key = entry.key
            val value = entry.value
            when (value) {
                null -> Log.d("HttpParameterEncoder", "Skipping field $key with null value")
                is JSONSerializable -> json.setObject(key, value.toJson())
                is Int -> json.setInt(key, value)
                is Double -> json.setDouble(key, value)
                is Float -> json.setDouble(key, value.toDouble())
                is BigDecimal -> json.setDecimal(key, value)
                is Boolean -> json.setBoolean(key, value)
                is String -> json.setString(key, value)
                is Date -> json.setDate(key, value)
                is ByteArray -> json.setString(key, Base64.encodeToString(value as ByteArray?, Base64.DEFAULT or Base64.NO_WRAP))
                is Map<*, *> ->
                    try {
                        json.setObject(key, jsonEncoded(value as Map<String, Any>))
                    } catch (error: Exception) {
                        throw JSONException(error.message)
                    }
                is List<*> -> {
                    json.setJSONArray(key, jsonEncoded(value as List<Any>))
                }
                is CustomStringConvertible -> json.setString(key, value.description)
                else -> {
                    Log.e("HttpParameterEncoder", "Invalid format for key $key")
                    throw JSONException("Invalid format for key ${entry.key}")
                }
            }
        }

        return json
    }

    @Throws(JSONException::class)
    private fun jsonEncoded(array: List<Any>?): JSONArray {
        val json = JSONArray()

        array?.forEach { value ->
            when (value) {
                is JSONSerializable -> json.add(value.toJson())
                is JSONObject -> json.add(value)
                is Int -> json.addInt(value)
                is Double -> json.addDouble(value)
                is Float -> json.addDouble(value.toDouble())
                is BigDecimal -> json.addDecimal(value)
                is Boolean -> json.addBoolean(value)
                is String -> json.addString(value)
                is Map<*, *> ->
                    try {
                        json.add(jsonEncoded(value as Map<String, Any>))
                    } catch (error: Exception) {
                        throw JSONException(error.message)
                    }
                is CustomStringConvertible -> json.addString(value.description)
                else -> throw JSONException("Invalid format")
            }
        }

        return json
    }
}

class HttpMultipartParameterEncoder(private val jsonEncoder: IHttpParameterEncoder) : IHttpParameterEncoder {

    override fun contentType(request: HttpRequest?): String {
        return "multipart/form-data"
    }

    @Throws(JSONException::class)
    override fun stringEncode(params: Map<String, Any?>): String {
        return jsonEncoder.stringEncode(params)
    }

    override fun dataEncode(params: Any): ByteArray {
        if (params !is MultipartBodyParam) { throw AnemoneException.notMappable }
        val body = tryOrNull { jsonEncoder.dataEncode(params.params) } ?: throw AnemoneException.notMappable
        return String(body, Charsets.UTF_8).replace("\n", "\r\n").toByteArray(Charsets.UTF_8)
    }
}
