package com.diegoparra.veggie.products.ui.utils

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StrikethroughSpan
import android.text.style.StyleSpan
import com.diegoparra.veggie.R
import com.diegoparra.veggie.products.domain.entities.Label
import java.util.*


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


fun getLabelProps(label: Label, context: Context) : Pair<String, ColorStateList>? {
    /*val colorNoStock = ResourcesCompat.getColor(resources, R.color.black_light, theme)
    val colorDiscount = ResourcesCompat.getColor(resources, R.color.red_700, theme)
    val additionalColors = resources.getIntArray(R.array.dark_colors)*/
    return when(label){
        is Label.NoStock -> {
            val colorNoStock = context.getColorFromAttr(R.attr.colorOnSurface)
            Pair(first = "Agotado", second = ColorStateList.valueOf(colorNoStock))
        }
        is Label.Discounted -> {
            val colorDiscount = context.getColorFromAttr(R.attr.colorSecondary)
            Pair(first = "${(label.discount*100).toInt()}% Off", second = ColorStateList.valueOf(colorDiscount))
        }
        is Label.DisplayLabel -> {
            val additionalColors = context.resources.getIntArray(context.getResourcesFromAttr(R.attr.colorsRandom))
            val color = when(label.suggestedLabel.toLowerCase(Locale.ROOT)){
                "recomendado" -> additionalColors[0]
                "popular" -> additionalColors.getOrElse(1) { additionalColors[0] }
                else -> additionalColors.getOrElse(2) { additionalColors[0] }
            }
            Pair(first = label.suggestedLabel, second = ColorStateList.valueOf(color))
        }
        is Label.Hidden -> null
    }
}