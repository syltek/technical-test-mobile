package com.playtomic.foundation.extension

public operator fun <K, V> MutableMap<K, V>.set(key: K, value: V?) {
    if (value != null) {
        put(key, value)
    } else {
        remove(key)
    }
}

val <K, V> Map<K, V>.nonEmpty: Map<K, V>?
    get() = if (isEmpty()) null else this

fun <K, V> Map<K, V>.firstOrNull(predicate: (Map.Entry<K, V>) -> Boolean): Map.Entry<K, V>? {
    for (element in this) {
        if (predicate(element)) {
            return element
        }
    }
    return null
}
