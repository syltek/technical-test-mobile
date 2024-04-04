package com.playtomic.foundation.executor

import android.os.Handler
import android.os.Looper
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

object PlaytomicExecutor {
    private const val DEFAULT_THREAD_POOL_SIZE = 4
    private val executorService: ExecutorService = Executors.newFixedThreadPool(DEFAULT_THREAD_POOL_SIZE)

    fun execute(runnable: () -> Unit) {
        executorService.execute(runnable)
    }
}

class Executor {
    class CancellableTask(private val job: () -> Unit, private val handler: Handler) : Runnable {
        enum class Status {
            waiting, completed, canceled
        }
        var status: Status = Status.waiting
            private set

        override fun run() {
            if (status == Status.waiting) {
                job()
                status = Status.completed
            }
        }

        fun cancel() {
            if (status == Status.waiting) {
                handler.removeCallbacks(this)
                status = Status.canceled
            }
        }
    }

    companion object {
        private var mainHandler = Handler(Looper.getMainLooper())

        var backgroundExecutor: (() -> Unit) -> Unit = {
            PlaytomicExecutor.execute(it)
        }

        var mainExecutor: (() -> Unit) -> Unit = {
            if (isMainThread) {
                it()
            } else {
                mainHandler.post(it)
            }
        }

        private val isMainThread: Boolean
            get() = Looper.myLooper() == Looper.getMainLooper()

        fun execute(after: Double = 0.0, inBackground: Boolean, job: () -> Unit): CancellableTask? {
            return if (after > 0 || inBackground || !isMainThread) {
                val executor = if (inBackground) backgroundExecutor else mainExecutor
                val task = CancellableTask({ executor(job) }, mainHandler)
                mainHandler.postDelayed(task, (after * 1000).toLong())
                task
            } else {
                job()
                null
            }
        }
    }
}
