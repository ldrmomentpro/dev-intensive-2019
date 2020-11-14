package ru.skillbranch.devintensive.extensions

import java.lang.Math.abs
import java.text.SimpleDateFormat
import java.time.Period
import java.util.*

const val SECOND = 1000L
const val MINUTE = SECOND * 60
const val HOUR = MINUTE * 60
const val DAY = HOUR * 24

fun Date.format(pattern: String = "HH:mm:ss dd.MM.yy") : String {
    val  dateFormat = SimpleDateFormat(pattern, Locale("ru"))
    return dateFormat.format(this)
}

fun Date.add(value: Int, units: TimeUnits) : Date {
    var time = this.time

    time += when(units) {
        TimeUnits.SECOND -> value * SECOND
        TimeUnits.MINUTE -> value * MINUTE
        TimeUnits.HOUR -> value * HOUR
        TimeUnits.DAY -> value * DAY
    }
    this.time = time
    return this
}

fun Date.humanizeDiff(date: Date = Date()): String {
    val currentDate = date
    val lastVisitDate = this
    val difference: Long = abs(currentDate.time - lastVisitDate.time)
    val differenceDates = difference
    val result = when(differenceDates) {
        in 0 until 1* SECOND -> "только что"
        in 1* SECOND until 45* SECOND -> "несколько секунд назад"
        in 45* SECOND until 75* SECOND -> "минуту назад"
        in 75* SECOND until 45* MINUTE -> "${(differenceDates/ MINUTE)} ${if (((differenceDates/ MINUTE) < 5) || ((differenceDates/ MINUTE) > 20 && (differenceDates/ MINUTE) < 25) || ((differenceDates/ MINUTE) > 30 && (differenceDates/ MINUTE) < 35) || ((differenceDates/ MINUTE) > 40 && (differenceDates/ MINUTE) < 45) ) "минуты" else "минут"} назад"
        in 45 * MINUTE until 75 * MINUTE -> "час назад"
        in 75* MINUTE until 22* HOUR -> "${(differenceDates/ HOUR)} ${if ((differenceDates/ HOUR) < 5 || (differenceDates/ HOUR) == 22.toLong()) "часа" else if ((differenceDates/ HOUR) == 21.toLong()) "час" else "часов"} назад"
        in 22* HOUR until 26* HOUR -> "день назад"
        in 26* HOUR until 360* DAY -> "${(differenceDates/ DAY)} ${if((differenceDates/ DAY) < 5 ) "дня" else "дней"} назад"
        in 360*DAY until Long.MAX_VALUE -> "более года назад"
        else -> throw IllegalStateException("invalid date")
    }
    return result.toString()
}

enum class TimeUnits {
    SECOND,
    MINUTE,
    HOUR,
    DAY
}