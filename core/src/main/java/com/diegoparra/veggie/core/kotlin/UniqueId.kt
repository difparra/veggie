package com.diegoparra.veggie.core.kotlin

import java.util.UUID

/**
 * Generate/Create a uniqueId
 */
fun getUniqueId(): String {
    return UUID.randomUUID().toString()
}