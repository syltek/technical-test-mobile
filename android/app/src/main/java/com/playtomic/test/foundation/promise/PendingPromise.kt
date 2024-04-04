package com.playtomic.foundation.promise

import org.jdeferred.impl.DeferredObject

/**
 * Created by agarcia on 14/06/2017.
 */
class PendingPromise<T> private constructor(val deferred: DeferredObject<T, Exception, Double>) :
    Promise<T>(deferred.promise()) {

    constructor() : this(deferred = DeferredObject<T, Exception, Double>())

    fun fulfill(value: T) {
        if (deferred.isResolved) {
            return
        }
        deferred.resolve(value)
    }

    fun reject(error: Exception) {
        if (deferred.isResolved) {
            return
        }
        deferred.reject(error)
    }
}
