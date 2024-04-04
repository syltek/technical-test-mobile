@file:Suppress("UNCHECKED_CAST")

package com.playtomic.foundation.extension

import com.playtomic.foundation.model.ResultTuple2
import com.playtomic.foundation.model.ResultTuple3
import com.playtomic.foundation.model.ResultTuple4

fun Any.asStringMap(): Map<String, Any>? =
    try {
        this as Map<String, Any>
    } catch (_: Exception) {
        null
    }

inline fun <T> tryOrNull(lambda: () -> T?): T? =
    try {
        lambda()
    } catch (_: Exception) {
        null
    }

inline fun <T> tryOr(value: T, lambda: () -> T): T =
    try {
        lambda()
    } catch (_: Exception) {
        value
    }

/**
 * Function to change range from [min, max] to [a,b]
 * f(x) = ((b-a)*(x-min)) / (max - min)
 */
fun reRange(fromMin: Double, fromMax: Double, toMin: Double, toMax: Double, value: Double) =
    ((toMax - toMin) * (value - fromMin)) / (fromMax - fromMin)

fun reRange(fromMin: Int, fromMax: Int, toMin: Int, toMax: Int, value: Double) =
    reRange(fromMin = fromMin.toDouble(), fromMax = fromMax.toDouble(), toMin = toMin.toDouble(), toMax = toMax.toDouble(), value = value)

inline fun <T1> guardLet(t1: T1?, elseClosure: () -> Nothing): T1 =
    t1 ?: elseClosure()

inline fun <T1, T2> guardLet(t1: T1?, t2: T2?, elseClosure: () -> Nothing): ResultTuple2<T1, T2> =
    if (t1 != null && t2 != null) {
        ResultTuple2(t1, t2)
    } else {
        elseClosure()
    }

inline fun <T1, T2, T3> guardLet(t1: T1?, t2: T2?, t3: T3?, elseClosure: () -> Nothing): ResultTuple3<T1, T2, T3> =
    if (t1 != null && t2 != null && t3 != null) {
        ResultTuple3(t1, t2, t3)
    } else {
        elseClosure()
    }

inline fun <T1, T2, T3, T4> guardLet(t1: T1?, t2: T2?, t3: T3?, t4: T4?, elseClosure: () -> Nothing): ResultTuple4<T1, T2, T3, T4> =
    if (t1 != null && t2 != null && t3 != null && t4 != null) {
        ResultTuple4(t1, t2, t3, t4)
    } else {
        elseClosure()
    }
