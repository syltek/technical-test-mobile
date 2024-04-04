@file:Suppress("UNCHECKED_CAST")

package com.playtomic.foundation.promise

import com.playtomic.foundation.executor.Executor
import com.playtomic.foundation.model.PlaytomicError
import com.playtomic.foundation.model.ResultTuple10
import com.playtomic.foundation.model.ResultTuple11
import com.playtomic.foundation.model.ResultTuple2
import com.playtomic.foundation.model.ResultTuple3
import com.playtomic.foundation.model.ResultTuple4
import com.playtomic.foundation.model.ResultTuple5
import com.playtomic.foundation.model.ResultTuple6
import com.playtomic.foundation.model.ResultTuple7
import com.playtomic.foundation.model.ResultTuple8
import com.playtomic.foundation.model.ResultTuple9
import org.jdeferred.android.AndroidDeferredManager

fun <T> whenAll(promises: List<Promise<T>>): Promise<List<T>> =
    if (promises.isEmpty()) {
        Promise(value = listOf())
    } else {
        Promise { fulfill, reject ->
            val internalPromises = promises.map { it.promise }
            val manager = AndroidDeferredManager()
            manager.`when`(*internalPromises.toTypedArray())
                .then { results ->
                    fulfill(results.toList().map { it.result as T })
                }
                .fail {
                    reject(it.reject as Exception)
                }
        }
    }

fun <T1, T2> whenAll(promise1: Promise<T1>, promise2: Promise<T2>): Promise<ResultTuple2<T1, T2>> =
    Promise { fulfill, reject ->
        val manager = AndroidDeferredManager()
        manager.`when`(promise1.promise, promise2.promise)
            .then { results ->
                fulfill(ResultTuple2(results[0].result as T1, results[1].result as T2))
            }
            .fail {
                reject(it.reject as Exception)
            }
    }

fun <T1, T2, T3> whenAll(promise1: Promise<T1>, promise2: Promise<T2>, promise3: Promise<T3>): Promise<ResultTuple3<T1, T2, T3>> =
    Promise { fulfill, reject ->
        val manager = AndroidDeferredManager()
        manager.`when`(promise1.promise, promise2.promise, promise3.promise)
            .then { results ->
                fulfill(ResultTuple3(results[0].result as T1, results[1].result as T2, results[2].result as T3))
            }
            .fail {
                reject(it.reject as Exception)
            }
    }

fun <T1, T2, T3, T4> whenAll(promise1: Promise<T1>, promise2: Promise<T2>, promise3: Promise<T3>, promise4: Promise<T4>): Promise<ResultTuple4<T1, T2, T3, T4>> =
    Promise { fulfill, reject ->
        val manager = AndroidDeferredManager()
        manager.`when`(promise1.promise, promise2.promise, promise3.promise, promise4.promise)
            .then { results ->
                fulfill(ResultTuple4(results[0].result as T1, results[1].result as T2, results[2].result as T3, results[3].result as T4))
            }
            .fail {
                reject(it.reject as Exception)
            }
    }

fun <T1, T2, T3, T4, T5> whenAll(
    promise1: Promise<T1>,
    promise2: Promise<T2>,
    promise3: Promise<T3>,
    promise4: Promise<T4>,
    promise5: Promise<T5>
): Promise<ResultTuple5<T1, T2, T3, T4, T5>> =
    Promise { fulfill, reject ->
        val manager = AndroidDeferredManager()
        manager.`when`(promise1.promise, promise2.promise, promise3.promise, promise4.promise, promise5.promise)
            .then { results ->
                fulfill(ResultTuple5(results[0].result as T1, results[1].result as T2, results[2].result as T3, results[3].result as T4, results[4].result as T5))
            }
            .fail {
                reject(it.reject as Exception)
            }
    }

fun <T1, T2, T3, T4, T5, T6> whenAll(
    promise1: Promise<T1>,
    promise2: Promise<T2>,
    promise3: Promise<T3>,
    promise4: Promise<T4>,
    promise5: Promise<T5>,
    promise6: Promise<T6>
): Promise<ResultTuple6<T1, T2, T3, T4, T5, T6>> =
    Promise { fulfill, reject ->
        val manager = AndroidDeferredManager()
        manager.`when`(promise1.promise, promise2.promise, promise3.promise, promise4.promise, promise5.promise, promise6.promise)
            .then { results ->
                fulfill(
                    ResultTuple6(
                        results[0].result as T1,
                        results[1].result as T2,
                        results[2].result as T3,
                        results[3].result as T4,
                        results[4].result as T5,
                        results[5].result as T6
                    )
                )
            }
            .fail {
                reject(it.reject as Exception)
            }
    }

fun <T1, T2, T3, T4, T5, T6, T7> whenAll(
    promise1: Promise<T1>,
    promise2: Promise<T2>,
    promise3: Promise<T3>,
    promise4: Promise<T4>,
    promise5: Promise<T5>,
    promise6: Promise<T6>,
    promise7: Promise<T7>
): Promise<ResultTuple7<T1, T2, T3, T4, T5, T6, T7>> =
    Promise { fulfill, reject ->
        val manager = AndroidDeferredManager()
        manager.`when`(promise1.promise, promise2.promise, promise3.promise, promise4.promise, promise5.promise, promise6.promise, promise7.promise)
            .then { results ->
                fulfill(
                    ResultTuple7(
                        results[0].result as T1,
                        results[1].result as T2,
                        results[2].result as T3,
                        results[3].result as T4,
                        results[4].result as T5,
                        results[5].result as T6,
                        results[6].result as T7
                    )
                )
            }
            .fail {
                reject(it.reject as Exception)
            }
    }

fun <T1, T2, T3, T4, T5, T6, T7, T8> whenAll(
    promise1: Promise<T1>,
    promise2: Promise<T2>,
    promise3: Promise<T3>,
    promise4: Promise<T4>,
    promise5: Promise<T5>,
    promise6: Promise<T6>,
    promise7: Promise<T7>,
    promise8: Promise<T8>
): Promise<ResultTuple8<T1, T2, T3, T4, T5, T6, T7, T8>> =
    Promise { fulfill, reject ->
        val manager = AndroidDeferredManager()
        manager.`when`(promise1.promise, promise2.promise, promise3.promise, promise4.promise, promise5.promise, promise6.promise, promise7.promise, promise8.promise)
            .then { results ->
                fulfill(
                    ResultTuple8(
                        results[0].result as T1,
                        results[1].result as T2,
                        results[2].result as T3,
                        results[3].result as T4,
                        results[4].result as T5,
                        results[5].result as T6,
                        results[6].result as T7,
                        results[7].result as T8
                    )
                )
            }
            .fail {
                reject(it.reject as Exception)
            }
    }

fun <T1, T2, T3, T4, T5, T6, T7, T8, T9> whenAll(
    promise1: Promise<T1>,
    promise2: Promise<T2>,
    promise3: Promise<T3>,
    promise4: Promise<T4>,
    promise5: Promise<T5>,
    promise6: Promise<T6>,
    promise7: Promise<T7>,
    promise8: Promise<T8>,
    promise9: Promise<T9>
): Promise<ResultTuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9>> =
    Promise { fulfill, reject ->
        val manager = AndroidDeferredManager()
        manager.`when`(promise1.promise, promise2.promise, promise3.promise, promise4.promise, promise5.promise, promise6.promise, promise7.promise, promise8.promise, promise9.promise)
            .then { results ->
                fulfill(
                    ResultTuple9(
                        results[0].result as T1,
                        results[1].result as T2,
                        results[2].result as T3,
                        results[3].result as T4,
                        results[4].result as T5,
                        results[5].result as T6,
                        results[6].result as T7,
                        results[7].result as T8,
                        results[8].result as T9
                    )
                )
            }
            .fail {
                reject(it.reject as Exception)
            }
    }

fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> whenAll(
    promise1: Promise<T1>,
    promise2: Promise<T2>,
    promise3: Promise<T3>,
    promise4: Promise<T4>,
    promise5: Promise<T5>,
    promise6: Promise<T6>,
    promise7: Promise<T7>,
    promise8: Promise<T8>,
    promise9: Promise<T9>,
    promise10: Promise<T10>
): Promise<ResultTuple10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>> =
    Promise { fulfill, reject ->
        val manager = AndroidDeferredManager()
        manager.`when`(
            promise1.promise, promise2.promise, promise3.promise, promise4.promise, promise5.promise,
            promise6.promise, promise7.promise, promise8.promise, promise9.promise, promise10.promise
        )
            .then { results ->
                fulfill(
                    ResultTuple10(
                        results[0].result as T1,
                        results[1].result as T2,
                        results[2].result as T3,
                        results[3].result as T4,
                        results[4].result as T5,
                        results[5].result as T6,
                        results[6].result as T7,
                        results[7].result as T8,
                        results[8].result as T9,
                        results[9].result as T10
                    )
                )
            }
            .fail {
                reject(it.reject as Exception)
            }
    }

fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> whenAll(
    promise1: Promise<T1>,
    promise2: Promise<T2>,
    promise3: Promise<T3>,
    promise4: Promise<T4>,
    promise5: Promise<T5>,
    promise6: Promise<T6>,
    promise7: Promise<T7>,
    promise8: Promise<T8>,
    promise9: Promise<T9>,
    promise10: Promise<T10>,
    promise11: Promise<T11>
): Promise<ResultTuple11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> =
    Promise { fulfill, reject ->
        val manager = AndroidDeferredManager()
        manager.`when`(
            promise1.promise, promise2.promise, promise3.promise, promise4.promise, promise5.promise,
            promise6.promise, promise7.promise, promise8.promise, promise9.promise, promise10.promise, promise11.promise
        )
            .then { results ->
                fulfill(
                    ResultTuple11(
                        results[0].result as T1,
                        results[1].result as T2,
                        results[2].result as T3,
                        results[3].result as T4,
                        results[4].result as T5,
                        results[5].result as T6,
                        results[6].result as T7,
                        results[7].result as T8,
                        results[8].result as T9,
                        results[9].result as T10,
                        results[10].result as T11
                    )
                )
            }
            .fail {
                reject(it.reject as Exception)
            }
    }

fun <T> resolve(retries: Int, delay: Double, retryCondition: (Exception) -> Boolean = { true }, promiseProvider: () -> Promise<T>): Promise<T> =
    Promise { fulfill, reject ->
        resolve(retries = retries, delay = delay, retryCondition = retryCondition, promiseProvider = promiseProvider, onFulfill = fulfill, onError = reject)
    }

private fun <T> resolve(
    retries: Int,
    delay: Double,
    retryCondition: (Exception) -> Boolean,
    promiseProvider: () -> Promise<T>,
    onFulfill: (T) -> Unit,
    onError: (Exception) -> Unit
) {
    promiseProvider()
        .then(onFulfill)
        .catchError { error ->
            if (retries > 0 && retryCondition(error)) {
                Executor.execute(after = delay, inBackground = false) {
                    resolve(retries = retries - 1, delay = delay, retryCondition = retryCondition, promiseProvider = promiseProvider, onFulfill = onFulfill, onError = onError)
                }
            } else {
                onError(error)
            }
        }
}

/**
 * If the given filter-lambda returns true, this method returns the current Promise,
 * otherwise it returns a failed Promise with the given error
 */
inline fun <E : Exception, D> Promise<D>.filterOrError(error: E, crossinline filter: (D) -> Boolean): Promise<D> =
    then { if (!filter(it)) throw error }

fun <D> Promise<D>.proceedIfFeatureFlag(featureFlag: Boolean): Promise<D> =
    filterOrError(error = PlaytomicError.featureFlagDisabled) { featureFlag }

/**
 * If Promise is in error, this method gives the possibility to use the Promise error to map it into a new one.
 */
inline fun <E : Exception, D> Promise<D>.mapError(crossinline map: (Exception) -> E): Promise<D> =
    fulfillOnError(promise = { Promise(error = map(it)) })
