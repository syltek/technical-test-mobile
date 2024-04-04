package com.playtomic.foundation.observable

import android.annotation.SuppressLint
import android.os.Looper
import androidx.annotation.AnyThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.map
import com.playtomic.foundation.promise.Promise

typealias Observable<T> = LiveData<T>
typealias MutableObservable<T> = ThreadMutableLiveData<T>

fun <T> Observable<T>.observe(lambda: ((T?) -> Unit)): Disposable {
    val observer = Observer<T>(lambda)
    this.observeForever(observer)
    return Disposable(dispose = { this.removeObserver(observer) })
}

fun <T> Observable<T>.singleValue(): Promise<T> =
    Promise<T> { fulfill, _ ->
        val currentValue = value
        if (currentValue != null) {
            fulfill(currentValue)
        } else {
            val disposal = Disposal()
            this.observe {
                if (it != null) {
                    disposal.dispose()
                    fulfill(it)
                }
            }.add(to = disposal)
        }
    }

fun <T> Observable<T>.whenValue(condition: (T) -> Boolean): Promise<T> =
    Promise<T> { fulfill, _ ->
        val currentValue = value
        if (currentValue != null && condition(currentValue)) {
            fulfill(currentValue)
        } else {
            val disposal = Disposal()
            this.observe {
                if (it != null && condition(it)) {
                    disposal.dispose()
                    fulfill(it)
                }
            }.add(to = disposal)
        }
    }

fun <T> Observable<T>.observeNext(observer: ((T?) -> Unit)): Disposable {
    return if (this.value == null) {
        observe(observer)
    } else {
        // Ignore first event since it is a previous value
        var isFirstEvent = true
        observe { value ->
            if (isFirstEvent) {
                isFirstEvent = false
            } else {
                observer(value)
            }
        }
    }
}

fun <T, R> Observable<T>.map(map: (T?) -> R): Observable<R> = this.map(map)

fun <T> Observable<T>.observeChange(observer: ((T?, T?) -> Unit)): Disposable {
    var previousValue = this.value
    return observeNext { newValue ->
        observer(previousValue, newValue)
        previousValue = newValue
    }
}

open class ThreadMutableLiveData<T> : MutableLiveData<T> {
    constructor() : super()
    constructor(value: T?) : super(value)

    var distinctValue: T?
        get() = value
        set(newValue) {
            if (value != newValue) {
                value = newValue
            }
        }

    @AnyThread
    @SuppressLint("WrongThread", "NullSafeMutableLiveData")
    override fun setValue(value: T?) {
        if (Looper.getMainLooper().thread == Thread.currentThread())
            super.setValue(value)
        else
            super.postValue(value)
    }
}
