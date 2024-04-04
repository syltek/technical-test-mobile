package com.playtomic.foundation.model

import com.playtomic.foundation.extension.addDays
import com.playtomic.foundation.extension.addMinutes
import com.playtomic.foundation.extension.addSeconds
import com.playtomic.foundation.extension.dateByAddingTimeZoneOffset
import com.playtomic.foundation.extension.dateByRemovingTimeZoneOffset
import com.playtomic.foundation.extension.toTime
import java.util.Date
import java.util.TimeZone

/**
 * Created by agarcia on 16/03/2017.
 */

open class DateRange {

    val from: Date
    val to: Date

    constructor(from: Date, to: Date) {
        this.from = from
        this.to = to
    }

    constructor(at: Date, spanMinutes: Int) {
        this.from = at.addMinutes(-spanMinutes)
        this.to = at.addMinutes(spanMinutes)
    }

    val spanMinutes: Int
        get() = (Math.round((to.time - from.time) / (2.0 * 60.0 * 1000))).toInt()

    override fun equals(other: Any?): Boolean {
        val dateRange = other as? DateRange ?: return false
        return to == dateRange.to && from == dateRange.from
    }

    override fun hashCode(): Int {
        var result = from.hashCode()
        result = 31 * result + to.hashCode()
        return result
    }

    public fun contains(date: Date): Boolean =
        from <= date && date <= to

    public fun allDates(spanMinutes: Int, includeLast: Boolean = false): List<Date> {
        val dates = mutableListOf<Date>()
        var lastDate = from
        while (lastDate < to) {
            dates.add(lastDate)
            lastDate = lastDate.addMinutes(spanMinutes)
        }
        if (includeLast && lastDate == to) {
            dates.add(lastDate)
        }
        return dates
    }

    fun addingMinutes(minutes: Int): DateRange =
        DateRange(from = from.addMinutes(minutes), to = to.addMinutes(minutes))

    fun addingDays(days: Int): DateRange =
        DateRange(from = from.addDays(days), to = to.addDays(days))
}

class TimeZoneDateRange : DateRange {
    companion object {}

    val timeZone: TimeZone

    constructor(from: Date, to: Date, timeZone: TimeZone = TimeZone.getDefault()) : super(from = from, to = to) {
        this.timeZone = timeZone
    }

    constructor(at: Date, spanMinutes: Int, timeZone: TimeZone = TimeZone.getDefault()) : super(at = at, spanMinutes = spanMinutes) {
        this.timeZone = timeZone
    }

    constructor(dateRange: DateRange, timeZone: TimeZone = TimeZone.getDefault()) : this(from = dateRange.from, to = dateRange.to, timeZone = timeZone)

    fun resetTimeZone(timeZone: TimeZone): TimeZoneDateRange {
        if (this.timeZone == timeZone) {
            return this
        }
        val dateFrom = this.from.dateByRemovingTimeZoneOffset(this.timeZone).dateByAddingTimeZoneOffset(timeZone)
        val dateTo = this.to.dateByRemovingTimeZoneOffset(this.timeZone).dateByAddingTimeZoneOffset(timeZone)
        return TimeZoneDateRange(from = dateFrom, to = dateTo, timeZone = timeZone)
    }

    override fun equals(other: Any?): Boolean {
        val timeZoneDateRange = other as? TimeZoneDateRange ?: return false
        return to == timeZoneDateRange.to && from == timeZoneDateRange.from && timeZone == timeZoneDateRange.timeZone
    }

    override fun hashCode(): Int {
        var result = from.hashCode()
        result = 31 * result + to.hashCode()
        return result
    }
}

fun DateRange.toTimeRange(timeZone: TimeZone) =
    TimeRange(from = from.toTime(timeZone = timeZone), to = to.toTime(timeZone = timeZone))

fun DateRange.openingRangeTo() = DateRange(from = from, to = to.addSeconds(-1))
