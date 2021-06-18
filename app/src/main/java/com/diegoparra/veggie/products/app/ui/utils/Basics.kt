package com.diegoparra.veggie.products.app.ui.utils

import android.content.Context
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StrikethroughSpan
import android.text.style.StyleSpan
import com.diegoparra.veggie.R
import com.diegoparra.veggie.core.android.appendMultipleSpans
import com.diegoparra.veggie.core.android.getColorFromAttr
import com.diegoparra.veggie.core.android.getColorWithAlphaFromAttrs
import com.diegoparra.veggie.core.kotlin.addPriceFormat
import com.diegoparra.veggie.core.kotlin.getValueBeforeDiscount


fun abbreviatedUnit(unit: String) =
    when (unit.lowercase()) {
        "libra" -> "lb"
        "unidad" -> "und"
        "canastilla" -> "cta"
        "bandeja" -> "bdj"
        else -> unit
    }

fun getFormattedPrice(
    finalPrice: Int, discount: Float, context: Context,
    hideDiscount: Boolean = false
): SpannableStringBuilder {
    val text = SpannableStringBuilder()
    if (discount > 0 && !hideDiscount) {
        val colorAlpha = context.getColorWithAlphaFromAttrs(
            colorAttr = R.attr.colorOnSurface,
            alphaAttr = R.attr.alphaSecondaryText
        )
        text.appendMultipleSpans(
            finalPrice.getValueBeforeDiscount(discount).addPriceFormat() + " ",
            listOf(StrikethroughSpan(), ForegroundColorSpan(colorAlpha)),
            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        )
        text.appendMultipleSpans(
            finalPrice.addPriceFormat(),
            listOf(
                StyleSpan(Typeface.BOLD),
                ForegroundColorSpan(context.getColorFromAttr(R.attr.colorSecondary))
            ),
            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        )
    } else {
        text.append(
            finalPrice.addPriceFormat(),
            StyleSpan(Typeface.BOLD),
            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        )
    }
    return text
}