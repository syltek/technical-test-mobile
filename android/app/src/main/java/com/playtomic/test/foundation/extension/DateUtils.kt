package com.playtomic.foundation.extension

import com.playtomic.foundation.model.Time
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit
import kotlin.math.abs

/**
 * Created by agarcia on 02/06/2017.
 */

object Dates {
    val UTC_TIME_ZONE = TimeZone.getTimeZone("UTC")

    const val defaultFormat = "yyyy-MM-dd'T'HH:mm:ss"

    fun from(day: Date, time: Date, timeZone: TimeZone? = null): Date {
        val dateComponents = Calendar.getInstance(timeZone ?: TimeZone.getDefault())
        dateComponents.timeInMillis = day.time

        val timeComponents = Calendar.getInstance(timeZone ?: TimeZone.getDefault())
        timeComponents.timeInMillis = time.time

        dateComponents.set(Calendar.HOUR_OF_DAY, timeComponents.get(Calendar.HOUR_OF_DAY))
        dateComponents.set(Calendar.MINUTE, timeComponents.get(Calendar.MINUTE))
        dateComponents.set(Calendar.SECOND, timeComponents.get(Calendar.SECOND))
        dateComponents.set(Calendar.MILLISECOND, timeComponents.get(Calendar.MILLISECOND))

        return dateComponents.time
    }

    fun from(string: String, format: String = defaultFormat, timeZone: TimeZone = TimeZone.getDefault()): Date? {
        val dateFormatter = SimpleDateFormat(format, Locale.US)
        dateFormatter.timeZone = timeZone
        return try {
            dateFormatter.parse(string)
        } catch (e: Exception) {
            null
        }
    }

    fun today(timeZone: TimeZone? = null): Date =
        Date().midnight(timeZone = timeZone)

    fun tomorrow(timeZone: TimeZone? = null): Date =
        today(timeZone = timeZone).addDays(1, timeZone)

    fun yesterday(timeZone: TimeZone? = null): Date =
        today(timeZone = timeZone).addDays(-1, timeZone)
}

@JvmOverloads
fun Date.midnight(timeZone: TimeZone? = null): Date {
    val calendar = Calendar.getInstance(timeZone ?: TimeZone.getDefault())
    calendar.timeInMillis = this.time
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)

    return calendar.time
}

fun Date.endOfDay(timeZone: TimeZone? = null): Date {
    val calendar = Calendar.getInstance(timeZone ?: TimeZone.getDefault())
    calendar.timeInMillis = this.time
    calendar.set(Calendar.HOUR_OF_DAY, 23)
    calendar.set(Calendar.MINUTE, 59)
    calendar.set(Calendar.SECOND, 59)

    return calendar.time
}

fun Date.startOfWeek(timeZone: TimeZone? = null): Date {
    val calendar = Calendar.getInstance(timeZone ?: TimeZone.getDefault())
    calendar.timeInMillis = this.time
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.clear(Calendar.MINUTE)
    calendar.clear(Calendar.SECOND)
    calendar.clear(Calendar.MILLISECOND)
    calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)

    return calendar.time
}

fun Date.endOfWeek(timeZone: TimeZone? = null): Date {
    val calendar = Calendar.getInstance(timeZone ?: TimeZone.getDefault())
    calendar.timeInMillis = this.time
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.clear(Calendar.MINUTE)
    calendar.clear(Calendar.SECOND)
    calendar.clear(Calendar.MILLISECOND)
    calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)

    calendar.add(Calendar.WEEK_OF_YEAR, 1)
    calendar.add(Calendar.SECOND, -1)

    return calendar.time
}

fun Date.startOfMonth(timeZone: TimeZone? = null): Date {
    val calendar = Calendar.getInstance(timeZone ?: TimeZone.getDefault())
    calendar.timeInMillis = this.time
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.clear(Calendar.MINUTE)
    calendar.clear(Calendar.SECOND)
    calendar.clear(Calendar.MILLISECOND)
    calendar.set(Calendar.DAY_OF_MONTH, 1)

    return calendar.time
}

fun Date.endOfMonth(timeZone: TimeZone? = null): Date {
    val calendar = Calendar.getInstance(timeZone ?: TimeZone.getDefault())
    calendar.timeInMillis = this.time
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.clear(Calendar.MINUTE)
    calendar.clear(Calendar.SECOND)
    calendar.clear(Calendar.MILLISECOND)
    calendar.set(Calendar.DAY_OF_MONTH, 1)

    calendar.add(Calendar.MONTH, 1)
    calendar.add(Calendar.SECOND, -1)

    return calendar.time
}

fun Date.addYears(years: Int): Date {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = this.time
    calendar.add(Calendar.YEAR, years)
    return calendar.time
}

fun Date.addDays(days: Int, timeZone: TimeZone? = null): Date {
    val calendar = Calendar.getInstance(timeZone ?: TimeZone.getDefault())
    calendar.timeInMillis = this.time
    calendar.add(Calendar.DAY_OF_YEAR, days)
    return calendar.time
}

fun Date.subtractDays(days: Int, timeZone: TimeZone? = null): Date = addDays(-days, timeZone)

fun Date.addHours(hours: Int): Date {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = this.time
    calendar.add(Calendar.HOUR_OF_DAY, hours)
    return calendar.time
}

fun Date.addMinutes(minutes: Int): Date {
    // TODO: Make this method threadsafe
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = this.time
    calendar.add(Calendar.MINUTE, minutes)
    return calendar.time
}

fun Date.addSeconds(seconds: Int): Date {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = this.time
    calendar.add(Calendar.SECOND, seconds)
    return calendar.time
}

fun Date.isMidnight(timeZone: TimeZone? = null): Boolean =
    midnight(timeZone) == this

fun Date.isToday(timeZone: TimeZone? = null): Boolean =
    isSameDay(Dates.today(timeZone = timeZone), timeZone = timeZone)

fun Date.isYesterday(timeZone: TimeZone? = null): Boolean =
    isSameDay(Dates.yesterday(timeZone = timeZone), timeZone = timeZone)

fun Date.isTomorrow(timeZone: TimeZone? = null): Boolean =
    isSameDay(Dates.tomorrow(timeZone = timeZone), timeZone = timeZone)

fun Date.isSameDay(day: Date, timeZone: TimeZone? = null): Boolean {
    val currentDate = Calendar.getInstance(timeZone ?: TimeZone.getDefault())
    currentDate.timeInMillis = this.time

    val anotherDay = Calendar.getInstance(timeZone ?: TimeZone.getDefault())
    anotherDay.timeInMillis = day.time

    val sameDay = currentDate.get(Calendar.DAY_OF_YEAR) == anotherDay.get(Calendar.DAY_OF_YEAR)
    val sameYear = currentDate.get(Calendar.YEAR) == anotherDay.get(Calendar.YEAR)

    return sameDay && sameYear
}

fun Date.isSameMonth(day: Date, timeZone: TimeZone? = null): Boolean {
    val currentDate = Calendar.getInstance(timeZone ?: TimeZone.getDefault())
    currentDate.timeInMillis = this.time

    val anotherDay = Calendar.getInstance(timeZone ?: TimeZone.getDefault())
    anotherDay.timeInMillis = day.time

    val sameMonth = currentDate.get(Calendar.MONTH) == anotherDay.get(Calendar.MONTH)
    val sameYear = currentDate.get(Calendar.YEAR) == anotherDay.get(Calendar.YEAR)

    return sameMonth && sameYear
}

fun Date.isSameYear(day: Date, timeZone: TimeZone? = null): Boolean {
    val currentDate = Calendar.getInstance(timeZone ?: TimeZone.getDefault())
    currentDate.timeInMillis = this.time

    val anotherDay = Calendar.getInstance(timeZone ?: TimeZone.getDefault())
    anotherDay.timeInMillis = day.time

    return currentDate.get(Calendar.YEAR) == anotherDay.get(Calendar.YEAR)
}

fun Date.dateByAddingTimeZoneOffset(timeZone: TimeZone): Date {
    val offset = timeZone.getOffset(this.time)
    return Date(this.time - offset)
}

fun Date.dateByRemovingTimeZoneOffset(timeZone: TimeZone): Date {
    val offset = timeZone.getOffset(this.time)
    return Date(this.time + offset)
}

fun Date.hours(to: Date) =
    TimeUnit.MILLISECONDS.toHours(to.time - this.time)

fun Date.minutes(to: Date) =
    TimeUnit.MILLISECONDS.toMinutes(to.time - this.time)

fun Date.seconds(to: Date) =
    TimeUnit.MILLISECONDS.toSeconds(to.time - this.time)

fun Date.millis(to: Date) =
    TimeUnit.MILLISECONDS.toMillis(to.time - this.time)

fun Date.years(to: Date): Int {
    val calendar = Calendar.getInstance()
    calendar.time = this
    val toCalendar = Calendar.getInstance()
    toCalendar.time = to
    var years = toCalendar.get(Calendar.YEAR) - calendar.get(Calendar.YEAR)
    if (calendar.get(Calendar.MONTH) > toCalendar.get(Calendar.MONTH) ||
        (calendar.get(Calendar.MONTH) == toCalendar.get(Calendar.MONTH) && calendar.get(Calendar.DAY_OF_MONTH) > toCalendar.get(Calendar.DAY_OF_MONTH))
    ) {
        years--
    }
    return years
}

fun Date.days(to: Date) =
    TimeUnit.MILLISECONDS.toDays(to.time - this.time)

fun Date.isFuture(): Boolean =
    this.after(Date())

fun Date.isPast(): Boolean =
    this.before(Date())

fun Date.dayOfYear(timeZone: TimeZone? = null): Int {
    val currentDate = Calendar.getInstance(timeZone ?: TimeZone.getDefault())
    return currentDate.get(Calendar.DAY_OF_YEAR)
}

fun Date.minutes(timeZone: TimeZone? = null): Int {
    val calendar = Calendar.getInstance(timeZone ?: TimeZone.getDefault())
    calendar.time = this
    return calendar.get(Calendar.MINUTE)
}

fun Date.hours(timeZone: TimeZone? = null): Int {
    val calendar = Calendar.getInstance(timeZone ?: TimeZone.getDefault())
    calendar.time = this
    return calendar.get(Calendar.HOUR_OF_DAY)
}

fun Date.seconds(timeZone: TimeZone? = null): Int {
    val calendar = Calendar.getInstance(timeZone ?: TimeZone.getDefault())
    calendar.time = this
    return calendar.get(Calendar.SECOND)
}

fun Date.toTime(timeZone: TimeZone): Time =
    Time.of(hour = hours(timeZone = timeZone), minute = minutes(timeZone = timeZone), second = seconds(timeZone = timeZone))

fun Date(timeZone: TimeZone): Date {
    val defaultTimeZone = TimeZone.getDefault()
    TimeZone.setDefault(timeZone)
    val date = Date()
    TimeZone.setDefault(defaultTimeZone)

    return date
}

fun Date.toString(format: String, timeZone: TimeZone): String {
    val dateFormatter = SimpleDateFormat(format, Locale.US)
    dateFormatter.timeZone = timeZone
    return dateFormatter.format(this)
}

fun Date.isSameWeekDay(day: Date, timeZone: TimeZone? = null): Boolean {
    val currentDate = Calendar.getInstance(timeZone ?: TimeZone.getDefault())
    currentDate.timeInMillis = this.time

    val anotherDay = Calendar.getInstance(timeZone ?: TimeZone.getDefault())
    anotherDay.timeInMillis = day.time

    return currentDate.get(Calendar.DAY_OF_WEEK) == anotherDay.get(Calendar.DAY_OF_WEEK)
}

fun Date.weeksTo(endDate: Date): Int =
    abs(((endDate.time - this.time) / 1000 / 3600 / 24 / 7).toInt())

@JvmOverloads
fun Date.formattedDateWithFormat(format: String, timeZone: TimeZone? = null): String {
    val dayFormatter = SimpleDateFormat(format, Locale.getDefault())
    dayFormatter.timeZone = timeZone ?: TimeZone.getDefault()

    return dayFormatter.format(this)
}

fun Date.beforeOrEqual(date: Date): Boolean =
    before(date) || equals(date)

fun Date.afterOrEqual(date: Date): Boolean =
    after(date) || equals(date)
