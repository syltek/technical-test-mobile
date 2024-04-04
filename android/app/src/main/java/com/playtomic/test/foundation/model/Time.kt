package com.playtomic.foundation.model

import java.util.*
import kotlin.math.abs

data class Time(
    val hour: Int,
    val minute: Int,
    val second: Int
) : CustomStringConvertible, Comparable<Time> {

    companion object {

        operator fun invoke(rawValue: String): Time {
            val components = rawValue.split(":")
            if (components.size == 2) {
                val hour = components[0].toInt()
                val minute = components[1].toInt()
                return Time(hour = hour, minute = minute, second = 0)
            }
            if (components.size == 3) {
                val hour = components[0].toInt()
                val minute = components[1].toInt()
                val second = components[2].toInt()
                return Time(hour = hour, minute = minute, second = second)
            } else {
                throw RuntimeException("Wrong time format for $rawValue")
            }
        }

        fun of(hour: Int = 0, minute: Int = 0, second: Int = 0): Time =
            Time(hour = hour, minute = minute, second = second)

        fun now(): Time {
            val calendar = Calendar.getInstance()
            return Time(
                hour = calendar.get(Calendar.HOUR_OF_DAY),
                minute = calendar.get(Calendar.MINUTE),
                second = calendar.get(Calendar.SECOND)
            )
        }

        fun first(): Time =
            Time.of()

        fun last(): Time =
            Time.of(hour = 23, minute = 59, second = 59)

        fun dayHours(): List<Time> =
            (0..23).map { Time.of(hour = it) }
    }

    override val description: String
        get() = "${String.format("%02d", hour)}:${String.format("%02d", minute)}:${String.format("%02d", second)}"

    fun adding(minutes: Int): Time {
        var hour = this.hour + minutes / 60
        var mins = this.minute + (minutes % 60)
        if (mins >= 60) {
            hour += 1
            mins -= 60
        }
        return Time(hour = hour, minute = mins, second = this.second)
    }

    fun removing(minutes: Int): Time {
        var hour = this.hour - (minutes / 60)
        var mins = minute - (minutes % 60)

        if (mins < 0) {
            hour -= 1
            mins += 60
        }

        return Time(hour = hour, minute = mins, second = second)
    }

    fun addingHours(hours: Int): Time = Time(hour = (this.hour + hours) % 24, minute = this.minute, second = this.second)

    // compares 2 times ignoring some seconds difference
    fun equivalent(time: Time, maxSeconds: Int = 60): Boolean =
        abs(toDaySeconds() - time.toDaySeconds()) <= maxSeconds

    private fun toDaySeconds(): Long =
        hour * 3600L + minute * 60L + second

    override fun compareTo(other: Time) =
        toDaySeconds().compareTo(other.toDaySeconds())
}

fun Time.Companion.generateTimes(fromTime: Time = Time.of(), toTime: Time = Time.of(hour = 23, minute = 59, second = 59), minutesInterval: Int): List<Time> {
    val times = mutableListOf<Time>()
    var lastTime = fromTime
    do {
        times.add(lastTime)
        lastTime = lastTime.adding(minutes = minutesInterval)
    } while (lastTime <= toTime)
    return times
}

fun Time.toDate(referenceDate: Date = Date(), timeZone: TimeZone): Date {
    val timeComponents = Calendar.getInstance(timeZone)
    timeComponents.timeInMillis = referenceDate.time
    timeComponents.set(Calendar.HOUR_OF_DAY, hour)
    timeComponents.set(Calendar.MINUTE, minute)
    timeComponents.set(Calendar.SECOND, second)
    timeComponents.set(Calendar.MILLISECOND, 0)

    return timeComponents.time
}
