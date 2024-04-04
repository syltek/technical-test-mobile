package com.playtomic.foundation.model

sealed class PlaytomicError : Exception() {
    object featureFlagDisabled : PlaytomicError()
}
