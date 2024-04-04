package com.anemonesdk.model.tenant

import com.playtomic.foundation.model.CustomStringConvertible

/**
 * Created by agarcia on 26/09/2017.
 */
enum class BookingType constructor(val rawValue: String) : CustomStringConvertible {
    PUBLIC("PUBLIC"),
    RESTRICTED("RESTRICTED"),
    PRIVATE("PRIVATE");

    override val description get() = rawValue

    companion object {
        fun fromRawValue(value: String): BookingType? = entries.firstOrNull { it.rawValue == value.uppercase() }
    }
}
