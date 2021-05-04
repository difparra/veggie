package com.diegoparra.veggie.products.ui.utils

import android.text.SpannableStringBuilder
import java.text.NumberFormat
import java.util.*
import kotlin.math.ceil


fun Int.addThousandSeparator() : String {
    return NumberFormat.getNumberInstance(Locale.US).format(this)
}

fun getPriceBeforeDiscount(finalPrice: Int, discount: Float) : Int {
    val price = finalPrice / (1-discount)
    return (ceil(price/10) * 10).toInt()
}

fun abbreviatedUnit(unit: String) =
        when (unit.toLowerCase(Locale.ROOT)){
            "libra" -> "lb"
            "unidad" -> "und"
            "canastilla" -> "cta"
            "bandeja" -> "bdj"
            else -> unit
        }

fun SpannableStringBuilder.appendMultipleSpans(text: CharSequence, what: List<Any>, flags: Int) : SpannableStringBuilder {
    if(what.isNullOrEmpty())    return this
    val start = this.length
    this.append(text)
    what.forEach { style ->
        setSpan(style, start, this.length, flags)
    }
    return this
}