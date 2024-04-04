package com.playtomic.foundation.model

import java.util.*

interface IClock {
    fun now(): Date = Date()
}

object Clock : IClock
