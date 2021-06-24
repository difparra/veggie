package com.diegoparra.veggie.core

import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAccessor
import java.util.Locale

//Date: return this.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL))
//Time: return this.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT))

private const val SPANISH_TAG = "es"


private fun TemporalAccessor.format(pattern: String, localeTag: String): String {
    val formatter = DateTimeFormatter.ofPattern(pattern, Locale.forLanguageTag(localeTag))
    return formatter.format(this)
}

fun LocalDate.appFormat(short: Boolean = false): String {
    val str = if (short) {
        this.format("eee, MMM d", SPANISH_TAG)
    } else {
        this.format("eeee, MMMM d", SPANISH_TAG)
    }
    //  Setting cap words in spanish locale
    val indexMonth = str.indexOfFirst { it == ',' } + 2
    return str.substring(0, 1).uppercase() +
            str.substring(1, indexMonth).lowercase() +
            str.substring(indexMonth, indexMonth + 1).uppercase() +
            str.substring(indexMonth + 1).lowercase()
}

fun LocalTime.appFormat(): String {
    return this.format("h:mm a", SPANISH_TAG)
}

fun Pair<LocalTime, LocalTime>.appFormat(): String {
    return if (first.isBefore(LocalTime.NOON) && second.isAfter(LocalTime.NOON)) {
        first.format("h:mm a", SPANISH_TAG) + " - " + second.format("h:mm a", SPANISH_TAG)
    } else {
        first.format("h:mm", SPANISH_TAG) + " - " + second.format("h:mm a", SPANISH_TAG)
    }
}