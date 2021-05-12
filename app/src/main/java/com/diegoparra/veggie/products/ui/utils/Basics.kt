package com.diegoparra.veggie.products.ui.utils

import android.content.Context
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StrikethroughSpan
import android.text.style.StyleSpan
import com.diegoparra.veggie.R
import com.diegoparra.veggie.core.getColorFromAttr
import com.diegoparra.veggie.core.getColorWithAlphaFromAttrs
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




fun getFormattedPrice(finalPrice: Int, discount: Float, context: Context,
                      hideDiscount: Boolean = false
) : SpannableStringBuilder {
    val text = SpannableStringBuilder()
    if(discount > 0 && !hideDiscount){
        val colorAlpha = context.getColorWithAlphaFromAttrs(colorAttr = R.attr.colorOnSurface, alphaAttr = R.attr.alphaSecondaryText)
        text.appendMultipleSpans(
                "$" + getPriceBeforeDiscount(finalPrice, discount).addThousandSeparator() + " ",
                listOf(StrikethroughSpan(), ForegroundColorSpan(colorAlpha)),
                Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        )
        text.appendMultipleSpans(
                "$" + finalPrice.addThousandSeparator(),
                listOf(StyleSpan(Typeface.BOLD), ForegroundColorSpan(context.getColorFromAttr(R.attr.colorSecondary))),
                Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        )
    }else{
        text.append(
                "$" + finalPrice.addThousandSeparator(),
                StyleSpan(Typeface.BOLD),
                Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        )
    }
    return text
}