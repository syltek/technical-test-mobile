package com.playtomic.foundation.model

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

enum class DayOfWeek constructor(val rawValue: String) : CustomStringConvertible {
    monday("MONDAY"),
    tuesday("TUESDAY"),
    wednesday("WEDNESDAY"),
    thursday("THURSDAY"),
    friday("FRIDAY"),
    saturday("SATURDAY"),
    sunday("SUNDAY")
    ;

    val isWeekend
        get() = when (this) {
            saturday, sunday -> true
            else -> false
        }

    override val description get() = rawValue

    fun formattedDay(format: String = "EEEE", locale: Locale = Locale.getDefault()) =
        SimpleDateFormat(format, locale).format(
            Calendar.getInstance().apply { set(Calendar.DAY_OF_WEEK, calendarDayOfWeek()) }.time
        )

    fun calendarDayOfWeek(): Int =
        when (this) {
            monday -> Calendar.MONDAY
            tuesday -> Calendar.TUESDAY
            wednesday -> Calendar.WEDNESDAY
            thursday -> Calendar.THURSDAY
            friday -> Calendar.FRIDAY
            saturday -> Calendar.SATURDAY
            sunday -> Calendar.SUNDAY
        }

    companion object {

        val all = listOf(monday, tuesday, wednesday, thursday, friday, saturday, sunday)

        operator fun invoke(rawValue: String): DayOfWeek? = entries.firstOrNull { it.rawValue == rawValue.uppercase() }

        operator fun invoke(calendarDay: Int): DayOfWeek? =
            when (calendarDay) {
                Calendar.MONDAY -> monday
                Calendar.TUESDAY -> tuesday
                Calendar.WEDNESDAY -> wednesday
                Calendar.THURSDAY -> thursday
                Calendar.FRIDAY -> friday
                Calendar.SATURDAY -> saturday
                Calendar.SUNDAY -> sunday
                else -> null
            }
    }
}

fun Date.dayOfWeek(): DayOfWeek {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = this.time
    val weekDay = calendar.get(Calendar.DAY_OF_WEEK)
    return DayOfWeek(weekDay) ?: throw Exception("Unexpected calendar week day $weekDay")
}
