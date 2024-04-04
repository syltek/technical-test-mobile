package com.playtomic.foundation.extension

import kotlin.math.max
import kotlin.math.min

/**
 * Created by manuelgonzalezvillegas on 30/5/17.
 */

fun <T, R> Iterable<T>.compactMap(transform: (T) -> R?): List<R> {
    val destination = mutableListOf<R>()
    for (element in this) {
        try {
            val obj = transform(element)
            if (obj != null) {
                destination.add(obj)
            }
        } catch (ex: Exception) {
        }
    }
    return destination
}

fun <T, R> Iterable<T>.flatMap(transform: (T) -> Iterable<R>?): List<R> {
    val destination = mutableListOf<R>()
    for (element in this) {
        val obj = transform(element)
        if (obj != null) {
            destination.addAll(obj)
        }
    }
    return destination
}

fun <K, V, R> Map<K, V>.compactMap(transform: (Map.Entry<K, V>) -> R?): List<R> {
    val destination = mutableListOf<R>()
    this.forEach { element ->
        try {
            val obj = transform(element)
            obj?.let {
                destination.add(it)
            }
        } catch (e: Exception) {
        }
    }
    return destination
}

fun <K, V, R> Map<K, V>.fold(initialValue: R, operation: (R, Map.Entry<K, V>) -> R): R {
    var result = initialValue
    this.forEach { element ->
        result = operation(result, element)
    }
    return result
}

inline fun <T, R> Iterable<T>.reduce(initial: R, operation: R.(T) -> R): R {
    var accumulator = initial
    for (element in this) accumulator = operation(accumulator, element)
    return accumulator
}

fun <K, V, R> Map<out K, V>.compactMapValues(transform: (V) -> R?): Map<K, R> {
    val destination = mutableMapOf<K, R>()
    this.forEach { element ->
        tryOrNull { transform(element.value) }?.let { destination[element.key] = it }
    }

    return destination
}

fun <T> Iterable<T>.unique(): List<T> =
    distinct()

fun <T> Iterable<T>.removing(elements: Iterable<T>): List<T> =
    this.filter { !elements.contains(it) }

fun <T> Iterable<T>.removing(element: T): List<T> {
    val index = indexOf(element)
    if (index >= 0) {
        val newArray = this.toMutableList()
        newArray.removeAt(index)
        return newArray
    }
    return this.toList()
}

fun <T> Iterable<T>.removingAll(element: T): List<T> {
    val newArray = this.toMutableList()
    while (newArray.contains(element)) {
        newArray.remove(element)
    }
    return newArray
}

fun <T> Iterable<T>.removingDuplicates(): List<T> {
    val result = mutableListOf<T>()
    this.forEach { value ->
        if (!result.contains(value)) {
            result.add(value)
        }
    }
    return result
}

fun <T> Iterable<T>.removing(at: Int): List<T> {
    val newArray = this.toMutableList()
    newArray.removeAt(at)
    return newArray
}

val <T> List<T>.nonEmpty: List<T>?
    get() = if (isEmpty()) null else this

fun <T> MutableList<T>.removeLast() {
    removeAt(size - 1)
}

fun <T> MutableList<T>.remove(at: Int) =
    removeAt(at)

fun <T> List<T>.removingLast(): List<T> =
    removingSubrange(fromIndex = this.size - 1)

fun <T> List<T>.removingSubrange(fromIndex: Int = 0, toIndex: Int = Int.MAX_VALUE): List<T> {
    val fromBoundedIndex = max(0, fromIndex)
    val toBoundedIndex = min(size, toIndex)
    if (fromBoundedIndex > toBoundedIndex) {
        return this
    }
    return this.filterIndexed { index, _ ->
        index < fromBoundedIndex || index >= toBoundedIndex
    }
}

fun <T> List<T>.replacingSubrange(fromIndex: Int, toIndex: Int, with: Collection<T>): List<T> {
    val fromBoundedIndex = max(0, fromIndex)
    var newList = mutableListOf<T>()
    newList.addAll(this)
    newList = newList.removingSubrange(fromIndex = fromBoundedIndex, toIndex = toIndex).toMutableList()
    newList.addAll(index = fromBoundedIndex, elements = with)
    return newList
}

fun <T> List<T>.firstIndexOf(predicate: (T) -> Boolean): Int? =
    this.indexOfFirst(predicate).let { if (it < 0) null else it }

fun <T> List<T>.appending(element: T): List<T> {
    val mutableList = toMutableList()
    mutableList.add(element)

    return mutableList
}

fun <T> Iterable<T>.firstIndex(of: T): Int? =
    indexOf(of).let { if (it < 0) null else it }

fun <T> Iterable<T>.findFirstIndexOrNull(predicate: (T) -> Boolean): Int? =
    indexOfFirst(predicate).let { if (it >= 0) it else null }

fun <T> List<T>.lastIndexOf(predicate: (T) -> Boolean): Int? =
    this.indexOfLast(predicate).let { if (it < 0) null else it }

fun <T> Iterable<T>.sorted(by: (T, T) -> Boolean): List<T> =
    sortedWith { a, b ->
        if (by(a, b)) -1 else 1
    }

inline fun <T, R1 : Comparable<R1>, R2 : Comparable<R2>> Iterable<T>.sortedBy(crossinline selector1: (T) -> R1?, crossinline selector2: (T) -> R2?): List<T> =
    sortedWith(compareBy(selector1).thenBy(selector2))

inline fun <T, R1 : Comparable<R1>, R2 : Comparable<R2>, R3 : Comparable<R3>> Iterable<T>.sortedBy(
    crossinline selector1: (T) -> R1?,
    crossinline selector2: (T) -> R2?,
    crossinline selector3: (T) -> R3?
): List<T> =
    sortedWith(compareBy(selector1).thenBy(selector2).thenBy(selector3))

fun <T> Iterable<T>.contains(predicate: (T) -> Boolean): Boolean =
    any(predicate)

fun <T, K> Iterable<T>.groupingBy(selector: (T) -> K): Map<K, List<T>> =
    groupingByKeys { listOf(selector(it)) }

fun <T, K> Iterable<T>.groupingByKeys(selector: (T) -> List<K>): Map<K, List<T>> {
    val dictionary = mutableMapOf<K, List<T>>()
    forEach { element ->
        selector(element).forEach { key ->
            val elements = dictionary[key]?.toMutableList() ?: mutableListOf()
            elements.add(element)
            dictionary[key] = elements
        }
    }
    return dictionary
}

fun <T, K> Iterable<T>.groupByDistinct(selector: (T) -> K?): Map<K, T> {
    val dictionary = mutableMapOf<K, T>()
    forEach { element ->
        val key = selector(element)
        if (key != null) {
            dictionary[key] = element
        }
    }
    return dictionary
}

fun <T> Iterable<T>.removingFirst(predicate: (T) -> Boolean): List<T> {
    val newList = this.toMutableList()
    val index = newList.firstIndexOf(predicate = predicate)
    if (index != null) {
        newList.remove(at = index)
    }
    return newList
}

fun <T> MutableList<T>.dropFirst(where: (T) -> Boolean): T? {
    val index = indexOfFirst(where)
    return if (index >= 0) {
        removeAt(index)
    } else {
        null
    }
}

fun <T, U> Iterable<U>.firstMap(transform: (U) -> T?): T? {
    forEach { element ->
        transform(element)?.let { newValue ->
            return newValue
        }
    }
    return null
}

fun <T> Iterable<T>.joined(separator: String): String =
    joinToString(separator = separator)

fun <T> MutableList<T>.swapAt(i: Int, j: Int): MutableList<T> {
    val temp = this[i]
    this[i] = this[j]
    this[j] = temp
    return this
}

fun <T> Iterable<T>.toIndexMap(): Map<T, Int> {
    val map = mutableMapOf<T, Int>()
    forEachIndexed { index, t ->
        map[t] = index
    }
    return map
}

val <T> List<T>.nullIfEmpty: List<T>?
    get() = ifEmpty { null }

inline fun <T, R1 : Comparable<R1>, R2 : Comparable<R2>, R3 : Comparable<R3>> compareBy(
    crossinline selector1: (T) -> R1?,
    crossinline selector2: (T) -> R2?,
    crossinline selector3: (T) -> R3?
): (T, T) -> Boolean {
    val comparator = compareBy(selector1).thenBy(selector2).thenBy(selector3)
    return { a, b -> comparator.compare(a, b) < 0 }
}

inline fun <T, R1 : Comparable<R1>, R2 : Comparable<R2>, R3 : Comparable<R3>, R4 : Comparable<R4>> compareBy(
    crossinline selector1: (T) -> R1?,
    crossinline selector2: (T) -> R2?,
    crossinline selector3: (T) -> R3?,
    crossinline selector4: (T) -> R4?
): (T, T) -> Boolean {
    val comparator = compareBy(selector1).thenBy(selector2).thenBy(selector3).thenBy(selector4)
    return { a, b -> comparator.compare(a, b) < 0 }
}
