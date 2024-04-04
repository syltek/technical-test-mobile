package com.playtomic.foundation.logger

object Log {
    enum class Level { Error, Warn, Info, Debug }
    var level: Level = Level.Warn

    val errorHandlers: MutableList<(tag: String, message: String, cause: Throwable?) -> Unit> = mutableListOf(
        { tag, message, cause -> android.util.Log.e(tag, "$message [ $cause ]") }
    )
    val warningHandlers: MutableList<(tag: String, message: String) -> Unit> = mutableListOf(
        { tag, message -> android.util.Log.w(tag, message) }
    )
    val infoHandlers: MutableList<(tag: String, message: String) -> Unit> = mutableListOf(
        { tag, message -> android.util.Log.i(tag, message) }
    )

    fun e(tag: String, message: String, cause: Throwable? = null) {
        if (level < Level.Error) return
        errorHandlers.forEach { it(tag, message, cause) }
    }

    fun w(tag: String, message: String) {
        if (level < Level.Warn) return
        warningHandlers.forEach { it(tag, message) }
    }

    // Info a message. Using lambda avoids computing the message if log level is not enabled. Useful for messages that are costly to produce
    fun i(tag: String, messageHandler: (() -> String)) {
        if (level < Level.Info) return
        infoHandlers.forEach { it(tag, messageHandler()) }
    }

    fun i(tag: String, message: String) {
        if (level < Level.Info) return
        infoHandlers.forEach { it(tag, message) }
    }

    // Debug a message. Using lambda avoids computing the message if log level is not enabled. Useful for messages that are costly to produce
    fun d(tag: String, messageHandler: (() -> String)) {
        if (level < Level.Debug) return
        android.util.Log.d(tag, messageHandler())
    }

    fun d(tag: String, message: String) {
        if (level < Level.Debug) return
        android.util.Log.d(tag, message)
    }
}
