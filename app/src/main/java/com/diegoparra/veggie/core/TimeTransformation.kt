package com.diegoparra.veggie.core

import com.diegoparra.veggie.core.kotlin.BasicTime
import java.time.LocalDateTime
import java.time.ZoneOffset

fun LocalDateTime.toBasicTime(): BasicTime {
    val instant = this.toInstant(ZoneOffset.UTC)
    return BasicTime.create(instant.epochSecond, instant.nano)
}

fun BasicTime.toLocalDateTime(): LocalDateTime {
    return LocalDateTime.ofEpochSecond(this.millisEpochUTC / 1000, 0, ZoneOffset.UTC)
}