package com.playtomic.foundation.extension

fun Double.isLess(than: Double) =
    this < than

fun Int.isMultiple(of: Int) = this.rem(of) == 0
