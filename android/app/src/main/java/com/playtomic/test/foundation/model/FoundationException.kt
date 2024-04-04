package com.playtomic.foundation.model

/**
 * Created by agarcia on 02/02/2017.
 */

sealed class FoundationException : Exception {
    constructor()
    constructor(message: String?) : super(message)
    constructor(cause: Exception) : super(cause)

    object timeout : FoundationException()
    object runtime : FoundationException()
    object denied : FoundationException()
}
