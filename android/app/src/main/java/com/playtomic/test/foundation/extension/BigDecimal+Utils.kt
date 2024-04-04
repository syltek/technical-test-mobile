package com.playtomic.foundation.extension

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.Locale
import kotlin.math.pow
import kotlin.math.roundToLong

operator fun BigDecimal.div(other: Int) =
    this / BigDecimal(other)

operator fun BigDecimal.times(other: Int) =
    this * BigDecimal(other)

fun BigDecimal.roundedValue(decimals: Int = 2): BigDecimal {
    val power = 10.0.pow(decimals.toDouble())
    val cents: Long = (this.toDouble() * power).roundToLong()
    val decimal = BigDecimal.valueOf(cents)
    return decimal.divide(BigDecimal(power)).stripTrailingZeros()
}

/**
 * Formats the [BigDecimal] with the specified locale, number of decimals, and option to keep trailing zeros.
 *
 * @param locale The locale to be used for formatting. If `null`, the default locale is used.
 * @param decimals The number of decimal places to round the value to (default = 2).
 * @param stripTrailingZero `true` to strip trailing zeros, `false` to keep them (default = true).
 * @return A formatted [String] representation of the [BigDecimal].
 */
fun BigDecimal.formattedDescription(locale: String? = null, decimals: Int = 2, stripTrailingZero: Boolean = true): String {
    val availableLocale: Locale = if (locale != null && locale.contains("-")) {
        val parts = locale.split("-")
        Locale(parts[0], parts[1])
    } else {
        Locale.getDefault()
    }
    val originalScale = this.scale()
    val fraction = this.divideAndRemainder(BigDecimal.ONE)[1]
    val roundedAmount =
        when {
            !stripTrailingZero && fraction == BigDecimal.ZERO -> this.setScale(1, RoundingMode.DOWN).abs()
            decimals == 0 || decimals > originalScale -> this.setScale(decimals, RoundingMode.DOWN).abs()
            else -> roundedValue(decimals).abs()
        }
    val formatter = DecimalFormat.getInstance(availableLocale)
    formatter.maximumFractionDigits = if (roundedAmount.scale() <= 0) 0 else decimals
    formatter.minimumFractionDigits =
        when {
            stripTrailingZero -> 0
            !stripTrailingZero && fraction == BigDecimal.ZERO -> 1
            else -> decimals
        }

    // \u00A0 is the NO-BREAK space, added by the formatter
    return (if (this < BigDecimal.ZERO) "-" else "") + formatter.format(roundedAmount)
        .replace("\u00A0", "")
}
