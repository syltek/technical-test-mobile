package com.playtomic.foundation.executor

import com.playtomic.foundation.observable.MutableObservable
import com.playtomic.foundation.observable.Observable
import com.playtomic.foundation.promise.PendingPromise
import com.playtomic.foundation.promise.Promise

class ExecutorQueue {
    val taskCount: Int get() = pendingTasks.count()
    val isRunning: Observable<Boolean>
        get() = _isRunning
    val isEmpty: Boolean get() = pendingTasks.isEmpty()
    private val _isRunning = MutableObservable<Boolean>(value = false)
    private var pendingTasks = mutableListOf<Task>()

    fun <T> add(promiseProvider: (() -> Promise<T>)): Promise<T> {
        val promiseTask = PromiseTask(promiseProvider = promiseProvider)
        add(task = promiseTask)
        return promiseTask.pendingPromise
    }

    private fun add(task: Task) {
        pendingTasks.add(task)
        if (isRunning.value == false) {
            processNext()
        }
    }

    private fun processNext() {
        if (!pendingTasks.isEmpty()) {
            _isRunning.distinctValue = true
            pendingTasks.removeAt(0).execute().always(this::processNext)
        } else {
            _isRunning.distinctValue = false
        }
    }
}

private interface Task {
    fun execute(): Promise<Unit>
}

private class PromiseTask<T>(
    val promiseProvider: (() -> Promise<T>)
) : Task {
    val pendingPromise: PendingPromise<T> = PendingPromise()

    override fun execute(): Promise<Unit> =
        promiseProvider()
            .then(pendingPromise::fulfill)
            .catchError(pendingPromise::reject)
            .then(map = { _ -> Unit })
            .fulfillOnError(value = Unit)
}
