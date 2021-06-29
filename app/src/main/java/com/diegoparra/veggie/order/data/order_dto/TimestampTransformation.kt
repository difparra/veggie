package com.diegoparra.veggie.order.data.order_dto

import com.google.firebase.Timestamp
import java.time.LocalDateTime
import java.time.ZoneOffset

/*
 *  Already in productsModule:
 *
 *  fun BasicTime.toTimestamp(): Timestamp =
 *      Timestamp(this.millisEpochUTC/1000, 0)
 *  fun Timestamp.toBasicTime(): BasicTime =
 *      BasicTime.create(seconds, nanoseconds)
 */

fun LocalDateTime.toTimestamp(): Timestamp {
    val instant = this.toInstant(ZoneOffset.UTC)
    return Timestamp(instant.epochSecond, instant.nano)
}

fun Timestamp.toLocalDateTime(): LocalDateTime {
    return LocalDateTime.ofEpochSecond(seconds, nanoseconds, ZoneOffset.UTC)
}