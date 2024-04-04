package com.playtomic.foundation.extension

import java.text.Normalizer
import java.util.*
import kotlin.math.max

fun String.replacingOccurrences(of: String, with: String) = replace(of, with)

/**
 * It capitalizes each word in the sentence
 */
val String.capitalized: String
    get() = this.lowercase()
        .split(" ")
        .map { part -> part.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() } }
        .joinToString(separator = " ")

val String.nonEmpty: String?
    get() = if (isEmpty()) null else this

val CharSequence.nonEmpty: CharSequence?
    get() = if (isEmpty()) null else this

fun String.wordWrapping(size: Int): String {
    if (this.length <= size) {
        return this
    }
    val wrap = substring(0, max(0, size))
    var parts = wrap.split(" ").removingLast()
    if (parts.isEmpty()) {
        val firstWord = split(" ").firstOrNull() ?: this
        parts = listOf(firstWord)
    }
    return parts.joinToString(separator = " ")
}

/**
 * It only capitalizes the first word in the sentence
 */
fun String.capitalizeFirstLetter(): String = this.replaceFirstChar {
    if (it.isLowerCase())
        it.titlecase(Locale.getDefault())
    else it.toString()
}

fun String.contains(other: String, ignoreCase: Boolean, ignoreDiacritic: Boolean): Boolean {
    val normalizedOther = if (ignoreDiacritic) other.unaccent() else other
    val normalizedSelf = if (ignoreDiacritic) this.unaccent() else this
    return normalizedSelf.contains(normalizedOther, ignoreCase = ignoreCase)
}

fun String.containsWord(starting: String): Boolean =
    " $this".contains(" $starting", ignoreCase = true, ignoreDiacritic = true)

private val REGEX_UNACCENT = "\\p{InCombiningDiacriticalMarks}+".toRegex()

fun String.unaccent(): String {
    val temp = Normalizer.normalize(this, Normalizer.Form.NFD)
    return REGEX_UNACCENT.replace(temp, "")
}

fun String.toBooleanOrNull(): Boolean? =
    when (this.lowercase()) {
        "true" -> true
        "false" -> false
        else -> null
    }

val String.numericAppVersion: Int?
    get() = this
        .split(".")
        .mapNotNull { it.toIntOrNull() }
        .takeIf { it.isNotEmpty() }
        ?.reduce { acc, s -> acc * 100 + s }
