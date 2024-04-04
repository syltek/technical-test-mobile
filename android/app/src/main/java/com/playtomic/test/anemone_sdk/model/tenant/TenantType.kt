package com.anemonesdk.model.tenant

import com.playtomic.foundation.model.CustomStringConvertible

enum class TenantType constructor(var rawValue: String) : CustomStringConvertible {
    syltekcrm("syltekcrm"),
    playtomicIntegrated("playtomic_integrated"),
    anemone("anemone");

    companion object {

        fun fromRawValue(rawValue: String): TenantType? = entries.firstOrNull { it.rawValue == rawValue.lowercase() }
    }

    override val description get() = rawValue
}
