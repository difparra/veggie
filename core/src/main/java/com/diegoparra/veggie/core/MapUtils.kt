package com.diegoparra.veggie.core

fun <A,B> MutableMap<A,B>.putIfNotNull(key: A, value: B?): Boolean {
    return value?.let {
        this[key] = it
        true
    } ?: false
}