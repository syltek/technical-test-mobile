package com.anemonesdk.model.generic

import com.playtomic.foundation.model.CustomStringConvertible
import org.json.JSONException

/**
 * Created by agarcia on 10/08/2017.
 */
open class Id : CustomStringConvertible {
    val value: String

    @Throws(JSONException::class)
    constructor(value: Any) {
        if (value is Int || value is Long) {
            this.value = value.toString()
        } else if (value is String) {
            this.value = value
        } else {
            throw JSONException("Expected ID type is Int or String")
        }
    }

    override val description get() = value

    override fun toString() = value

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        return (other as? Id)?.value == value
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }
}
