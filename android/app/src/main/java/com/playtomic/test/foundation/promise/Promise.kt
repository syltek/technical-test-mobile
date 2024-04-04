package com.playtomic.foundation.promise

import com.playtomic.foundation.executor.Executor
import com.playtomic.foundation.extension.tryOrNull
import com.playtomic.foundation.model.FoundationException
import org.jdeferred.android.AndroidDeferredManager
import org.jdeferred.impl.DeferredObject

/**
 * Created by mgonzalez on 20/12/16.
 */

open class Promise<D> {
    companion object {
        val mainManager = AndroidDeferredManager()
    }

    internal val promise: org.jdeferred.Promise<D, Exception, Double>

    var value: D? = null

    var error: Exception? = null

    val isResolved: Boolean
        get() = value != null || error != null

    constructor(value: D) {
        val deferred = DeferredObject<D, Exception, Double>()
        promise = mainManager.`when`(deferred.promise()).then { this.value = it }.fail { this.error = it }
        deferred.resolve(value)
    }

    constructor(error: Exception) {
        val deferred = DeferredObject<D, Exception, Double>()
        promise = mainManager.`when`(deferred.promise()).then { this.value = it }.fail { this.error = it }
        deferred.reject(error)
    }

    constructor(after: Double = 0.0, executeInBackground: Boolean = false, resolver: (fulfill: (D) -> Unit, reject: (Exception) -> Unit) -> Unit) {
        val deferred = DeferredObject<D, Exception, Double>()
        promise = mainManager.`when`(deferred).then { this.value = it }.fail { this.error = it }
        Executor.execute(after = after, inBackground = executeInBackground) {
            try {
                resolver({
                    if (!deferred.isResolved) {
                        deferred.resolve(it)
                    }
                }, {
                    if (!deferred.isResolved) {
                        deferred.reject(it)
                    }
                })
            } catch (e: Exception) {
                if (!deferred.isResolved) {
                    deferred.reject(e)
                }
            }
        }
    }

    protected constructor(promise: org.jdeferred.Promise<D, Exception, Double>) {
        this.promise = mainManager.`when`(promise).then { this.value = it }.fail { this.error = it }
    }

    fun catchError(callback: (Exception) -> Unit): Promise<D> =
        Promise<D>(promise.fail(callback))

    fun fulfillOnError(promise: (Exception) -> Promise<D>): Promise<D> =
        Promise { fulfill, reject ->
            this.then(fulfill)
                .catchError { error ->
                    promise(error)
                        .then(fulfill)
                        .catchError(reject)
                }
        }

    fun fulfillOnError(value: D): Promise<D> =
        Promise { fulfill, _ ->
            this
                .then(fulfill)
                .catchError { fulfill(value) }
        }

    @JvmName("fulfillOnErrorWithNullable")
    fun fulfillOnError(map: (Exception) -> D?): Promise<D> =
        Promise { fulfill, reject ->
            this.then(fulfill).catchError { error ->
                val value = tryOrNull { map(error) }
                if (value != null) {
                    fulfill(value)
                } else {
                    reject(error)
                }
            }
        }

    fun then(execute: (D) -> Unit): Promise<D> =
        then(inBackground = false, execute = execute)

    fun then(inBackground: Boolean, execute: (D) -> Unit): Promise<D> {
        val deferred = DeferredObject<D, Exception, Double>()
        this.promise.then { result: D ->
            Executor.execute(inBackground = inBackground) {
                try {
                    execute(result)
                    deferred.resolve(result)
                } catch (e: Exception) {
                    deferred.reject(e)
                }
            }
        }.fail { deferred.reject(it) }
        return Promise(deferred)
    }

    @JvmName("thenMap")
    fun <U> then(map: (D) -> U): Promise<U> =
        then(inBackground = false, map = map)

    @JvmName("thenMapInBackground")
    fun <U> then(inBackground: Boolean, map: (D) -> U): Promise<U> {
        val deferred = DeferredObject<U, Exception, Double>()
        this.promise.then { result: D ->
            Executor.execute(inBackground = inBackground) {
                try {
                    val newObject = map(result)
                    deferred.resolve(newObject)
                } catch (e: Exception) {
                    deferred.reject(e)
                }
            }
        }.fail { deferred.reject(it) }
        return Promise(deferred)
    }

    @JvmName("thenPromise")
    fun <D_OUT> then(promise: (D) -> Promise<D_OUT>): Promise<D_OUT> =
        then(inBackground = false, promise = promise)

    @JvmName("thenPromiseInBackground")
    fun <D_OUT> then(inBackground: Boolean, promise: (D) -> Promise<D_OUT>): Promise<D_OUT> {
        val deferred = DeferredObject<D_OUT, Exception, Double>()
        this.promise
            .then { result: D ->
                Executor.execute(inBackground = inBackground) {
                    try {
                        val deferredPromise = promise(result)
                        deferredPromise
                            .then(deferred::resolve)
                            .catchError(deferred::reject)
                    } catch (e: Exception) {
                        deferred.reject(e)
                    }
                }
            }
            .fail { deferred.reject(it) }

        return Promise(deferred)
    }

    fun always(callback: () -> Unit): Promise<D> =
        always(inBackground = false, callback = callback)

    fun always(inBackground: Boolean, callback: () -> Unit): Promise<D> {
        // agarcia: We use then/fail instead of always because JDeferred executes always callback as last step and not in setup order as expected
        val deferred = DeferredObject<D, Exception, Double>()
        this.promise.then { result: D ->
            Executor.execute(inBackground = inBackground) {
                callback()
                deferred.resolve(result)
            }
        }.fail {
            Executor.execute(inBackground = inBackground) {
                callback()
                deferred.reject(it)
            }
        }
        return Promise(deferred)
    }

    fun rejectOnTimeout(timeout: Double): Promise<D> {
        val deferred = DeferredObject<D, Exception, Double>()
        val task = Executor.execute(after = timeout, inBackground = false) {
            if (deferred.isPending) {
                deferred.reject(FoundationException.timeout)
            }
        }
        promise
            .then { result: D ->
                if (deferred.isPending) {
                    task?.cancel()
                    deferred.resolve(result)
                }
            }
            .fail {
                if (deferred.isPending) {
                    task?.cancel()
                    deferred.reject(it)
                }
            }
        return Promise(deferred)
    }

    fun ignoreResult(): Promise<Unit> = then(map = {})
}
