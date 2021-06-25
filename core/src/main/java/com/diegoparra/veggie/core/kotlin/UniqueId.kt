package com.diegoparra.veggie.core.kotlin

import java.util.UUID

fun generateUniqueId(): String {
    return UUID.randomUUID().toString()
}