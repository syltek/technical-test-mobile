package com.playtomic.foundation.model

import com.playtomic.foundation.extension.addDays
import java.util.Date
import java.util.TimeZone

class TimeRange : CustomStringConvertible {
    val from: Time
    val to: Time

    val isFullDay: Boolean
        get() = equivalent(TimeRange.fullDay())

    constructor(from: Time, to: Time) {
        this.from = from
        this.to = to
    }

    constructor(rawValue: String) {
        val components = rawValue.split("-")
        if (components.size != 2) {
            throw RuntimeException("Wrong time range format for $rawValue")
        }
        this.from = Time(components[0])
        this.to = Time(components[1])
    }

    fun intersects(timeRange: TimeRange): Boolean =
        timeRange.contains(this) || this.contains(timeRange) ||
            (to > timeRange.from && to < timeRange.to) ||
            (timeRange.to > from && timeRange.to < to)

    fun contains(timeRange: TimeRange): Boolean =
        this.from <= timeRange.from && this.to >= timeRange.to

    override fun equals(other: Any?): Boolean =
        other is TimeRange && other.from == from && other.to == to

    override fun hashCode(): Int {
        var result = from.hashCode()
        result = 31 * result + to.hashCode()
        return result
    }

    override val description: String
        get() = "${from.description}-${to.description}"

    fun contains(time: Time): Boolean =
        this.from <= time && this.to >= time

    // compares 2 timeranges ignoring some seconds difference
    fun equivalent(timeRange: TimeRange, maxSeconds: Int = 60): Boolean =
        from.equivalent(timeRange.from, maxSeconds = maxSeconds) && to.equivalent(timeRange.to, maxSeconds = maxSeconds)

    companion object {
        fun fullDay(): TimeRange =
            TimeRange(from = Time.first(), to = Time.last())
    }
}

fun TimeRange.toDateRange(referenceDate: Date = Date(), timeZone: TimeZone): DateRange {
    val fromDate = from.toDate(referenceDate = referenceDate, timeZone = timeZone)
    val toDate = to.toDate(referenceDate = referenceDate, timeZone = timeZone)
    return DateRange(from = fromDate, to = if (toDate < fromDate) toDate.addDays(1) else toDate)
}

fun TimeRange.croppingTo(): TimeRange =
    TimeRange(
        from = from,
        to = if (from > to) Time.of(hour = 23, minute = 59, second = 59) else to
    )

fun TimeRange.croppingFrom(): TimeRange =
    TimeRange(
        from = if (from > to) Time.of(hour = 0, minute = 0, second = 0) else from,
        to = to
    )

fun TimeRange.excludingTo(): TimeRange =
    TimeRange(from = from, to = to.removing(minutes = 1))


fun List<TimeRange>.mergeConsecutiveRanges(): List<TimeRange> {
    val sortedRanges = this.sortedBy { it.from.hour }.toMutableList()
    if (sortedRanges.size < 2) {
        return sortedRanges
    }

    val mergedRanges = mutableListOf(sortedRanges[0])
    for (i in 1 until sortedRanges.size) {
        val current = sortedRanges[i]
        val previous = mergedRanges.last()

        if (current.from.hour - previous.to.hour.plus(1) == 0) {
            val merged = TimeRange(from = previous.from, to = current.to)
            sortedRanges.removeAll(listOf(previous, current))
            sortedRanges.add(mergedRanges.lastIndex, merged)
            return sortedRanges.mergeConsecutiveRanges()
        } else {
            mergedRanges.add(current)
        }
    }
    return mergedRanges
}
