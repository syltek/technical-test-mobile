package com.playtomic.foundation.extension

import com.playtomic.foundation.model.ResultTuple2
import com.playtomic.foundation.model.ResultTuple3
import com.playtomic.foundation.model.ResultTuple4
import com.playtomic.foundation.promise.Promise

/**
 * If this is null, it returns the given not-nullable parameter otherwise it returns this.
 * 'value: A' is evaluated anyway, even if discarded, so better use .or(takeValue: () -> A) when working with Promises.
 */
infix fun <A : Any> A?.or(value: A): A =
    this ?: value

/**
 * If this is null, it returns the computation of the given lambda otherwise it returns this.
 * This implementation is Promise friendly.
 */
inline fun <A : Any> A?.or(takeValue: () -> A): A =
    this ?: takeValue()

/**
 * If this is null, it returns the given nullable parameter otherwise it returns this
 */
infix fun <A : Any> A?.orNullable(value: A?): A? =
    this ?: value

/**
 * If this is null, the lambda param is executed, otherwise not
 */
inline fun <A : Any> A?.ifNullExecute(expression: () -> Unit): A? {
    if (this == null) expression()
    return this
}

/**
 * Transform a nullable A into a Promise<A>.
 * If A is null, the resulted Promise will be a failed one with specified error.
 */
fun <A : Any> A?.toPromise(error: Exception): Promise<A> =
    this?.let { Promise(value = it) }.or(takeValue = { Promise(error = error) })

/**
 * If all the given values are not null, it returns a tuple within all parameters, otherwise it returns nil
 */
fun <A : Any, B : Any> allNotNull(value1: A?, value2: B?): ResultTuple2<A, B>? =
    when {
        value1 != null && value2 != null -> ResultTuple2(value1, value2)
        else -> null
    }

/**
 * If all the given values are not null, it returns a tuple within all parameters, otherwise it returns nil
 */
fun <A : Any, B : Any, C : Any> allNotNull(value1: A?, value2: B?, value3: C?): ResultTuple3<A, B, C>? =
    allNotNull(allNotNull(value1, value2), value3)
        ?.let { (tuple, third) -> ResultTuple3(tuple.value1, tuple.value2, third) }

/**
 * If all the given values are not null, it returns a tuple within all parameters, otherwise it returns nil
 */
fun <A : Any, B : Any, C : Any, D : Any> allNotNull(value1: A?, value2: B?, value3: C?, value4: D?): ResultTuple4<A, B, C, D>? =
    allNotNull(allNotNull(value1, value2, value3), value4)
        ?.let { (tuple, fourth) -> ResultTuple4(tuple.value1, tuple.value2, tuple.value3, fourth) }
