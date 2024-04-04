package com.anemonesdk.general.storage.preferences

import android.content.Context
import android.content.SharedPreferences
import com.anemonesdk.general.storage.IKeyValueStorage
import com.playtomic.foundation.extension.tryOrNull

/**
 * Created by mgonzalez on 15/12/16.
 */

class UserDefaultsStorage(context: Context) : IKeyValueStorage {

    private val preferences: SharedPreferences

    init {
        preferences = context.getSharedPreferences("MySportsPreferences", Context.MODE_PRIVATE)
    }

    override fun getBool(name: String): Boolean? =
        tryOrNull {
            if (preferences.contains(name)) preferences.getBoolean(name, false) else null
        }

    override fun setBool(name: String, value: Boolean?) {
        val editor = preferences.edit()
        if (value == null) {
            editor.remove(name)
        } else {
            editor.putBoolean(name, value)
        }
        editor.apply()
    }

    override fun getString(name: String): String? =
        tryOrNull {
            preferences.getString(name, null)
        }

    override fun setString(name: String, value: String?) {
        val editor = preferences.edit()
        editor.putString(name, value)
        editor.apply()
    }

    override fun getInt(name: String): Int? =
        tryOrNull {
            if (preferences.contains(name)) preferences.getInt(name, 0) else null
        }

    override fun setInt(name: String, value: Int?) {
        val editor = preferences.edit()
        if (value == null) {
            editor.remove(name)
        } else {
            editor.putInt(name, value)
        }
        editor.apply()
    }

    override fun getDouble(name: String): Double? =
        tryOrNull {
            if (preferences.contains(name)) preferences.getFloat(name, 0.0f).toDouble() else null
        }

    override fun setDouble(name: String, value: Double?) {
        val editor = preferences.edit()
        if (value == null) {
            editor.remove(name)
        } else {
            editor.putFloat(name, value.toFloat())
        }
        editor.apply()
    }

    override fun getLong(name: String): Long? =
        tryOrNull {
            if (preferences.contains(name)) preferences.getLong(name, 0) else null
        }

    override fun setLong(name: String, value: Long?) {
        val editor = preferences.edit()
        if (value == null) {
            editor.remove(name)
        } else {
            editor.putLong(name, value)
        }
        editor.apply()
    }

    override fun getData(name: String): ByteArray? =
        getString(name)?.toByteArray()

    override fun setData(name: String, value: ByteArray?) {
        setString(name = name, value = value?.let { String(it) })
    }
}
