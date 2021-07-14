package com.diegoparra.veggie.products.app.ui.utils

import android.content.Context
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StrikethroughSpan
import android.text.style.StyleSpan
import androidx.annotation.AttrRes
import com.diegoparra.veggie.R
import com.diegoparra.veggie.core.android.appendMultipleSpans
import com.diegoparra.veggie.core.android.getColorFromAttr
import com.diegoparra.veggie.core.android.getColorWithAlphaFromAttrs
import com.diegoparra.veggie.core.kotlin.addPriceFormat
import com.diegoparra.veggie.core.kotlin.getValueBeforeDiscount
import timber.log.Timber


fun abbreviatedPacket(packet: String) =
    when (packet.lowercase()) {
        "libra" -> "lb"
        "unidad" -> "und"
        "canastilla" -> "cta"
        "bandeja" -> "bdj"
        else -> packet
    }

fun getFormattedPrice(
    finalPrice: Int, discount: Float,
    context: Context, ssb: SpannableStringBuilder = SpannableStringBuilder(),
    @AttrRes colorStrike: Int = R.attr.colorOnSurface,
    @AttrRes alphaStrike: Int? = R.attr.alphaSecondaryText,
    @AttrRes colorPriceFinal: Int = R.attr.colorSecondary
): SpannableStringBuilder {
    return if (discount > 0) {
        getFormattedPrice(
            finalPrice = finalPrice,
            priceBeforeDiscount = finalPrice.getValueBeforeDiscount(discount),
            context = context, ssb = ssb,
            colorStrike = colorStrike, alphaStrike = alphaStrike,
            colorPriceFinal = colorPriceFinal
        )
    } else {
        ssb.append(
            finalPrice.addPriceFormat(),
            StyleSpan(Typeface.BOLD),
            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        )
    }
}

fun getFormattedPrice(
    finalPrice: Int, priceBeforeDiscount: Int,
    context: Context, ssb: SpannableStringBuilder = SpannableStringBuilder(),
    @AttrRes colorStrike: Int = R.attr.colorOnSurface,
    @AttrRes alphaStrike: Int? = R.attr.alphaSecondaryText,
    @AttrRes colorPriceFinal: Int = R.attr.colorSecondary
): SpannableStringBuilder {
    //  Check they are not the same, and price before discount (the one stroked) has sense (is greater than discounted one)
    if (priceBeforeDiscount > finalPrice) {
        ssb.appendMultipleSpans(
            priceBeforeDiscount.addPriceFormat() + " ",
            listOf(
                StrikethroughSpan(),
                ForegroundColorSpan(
                    context.getColorWithAlphaFromAttrs(
                        colorAttr = colorStrike,
                        alphaAttr = alphaStrike
                    )
                )
            ),
            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        )
        ssb.appendMultipleSpans(
            finalPrice.addPriceFormat(),
            listOf(
                StyleSpan(Typeface.BOLD),
                ForegroundColorSpan(context.getColorWithAlphaFromAttrs(colorAttr = colorPriceFinal))
            ),
            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        )
    } else {
        ssb.append(
            finalPrice.addPriceFormat(),
            StyleSpan(Typeface.BOLD),
            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        )
    }
    return ssb
}