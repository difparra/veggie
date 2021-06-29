package com.diegoparra.veggie.products.data

import com.diegoparra.veggie.core.kotlin.BasicTime
import com.google.firebase.Timestamp

fun BasicTime.toTimestamp(): Timestamp =
    Timestamp(this.millisEpochUTC/1000, 0)

fun Timestamp.toBasicTime(): BasicTime =
    BasicTime.create(seconds, nanoseconds)