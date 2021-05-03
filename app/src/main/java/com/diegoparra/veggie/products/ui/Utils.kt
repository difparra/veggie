package com.diegoparra.veggie.products.ui

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StrikethroughSpan
import android.text.style.StyleSpan
import android.util.TypedValue
import androidx.annotation.AttrRes
import com.diegoparra.veggie.R
import com.diegoparra.veggie.products.domain.entities.Description
import com.diegoparra.veggie.products.domain.entities.Label
import java.text.NumberFormat
import java.util.*
import kotlin.math.ceil

/*
    This file contain kind of resources.
    This contains some strings and colors that could be changed by the designer.
 */

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

fun getWeightString(weightGr: Int) : String? =
    if(weightGr == -1) null
    else "Aprox. ${weightGr}g"

//@ColorRes @ArrayRes
fun Context.getResourceFromAttr(
    @AttrRes attrColor: Int,
    typedValue: TypedValue = TypedValue(),
    resolveRefs: Boolean = true
) : Int{
    theme.resolveAttribute(attrColor, typedValue, resolveRefs)
    return typedValue.data
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



//      MAIN PRODUCTS ADAPTER HELPERS       --------------------------------------------------------


fun getLabelProps(label: Label, context: Context) : Pair<String, ColorStateList>? {
    /*val colorNoStock = ResourcesCompat.getColor(resources, R.color.black_light, theme)
    val colorDiscount = ResourcesCompat.getColor(resources, R.color.red_700, theme)
    val additionalColors = resources.getIntArray(R.array.dark_colors)*/
    return when(label){
        is Label.NoStock -> {
            val colorNoStock = context.getResourceFromAttr(R.attr.colorOnSurface)
            Pair(first = "Agotado", second = ColorStateList.valueOf(colorNoStock))
        }
        is Label.Discounted -> {
            val colorDiscount = context.getResourceFromAttr(R.attr.colorSecondary)
            Pair(first = "${(label.discount*100).toInt()}% Off", second = ColorStateList.valueOf(colorDiscount))
        }
        is Label.DisplayLabel -> {
            val additionalColors = context.resources.getIntArray(context.getResourceFromAttr(R.attr.colorsRandom))
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




fun getDescriptionText(description: Description, context: Context) : Spannable {
    with(description){
        return when(this){
            is Description.Discounted -> getDescriptionDiscounted(finalPrice, discount, unit, context)
            is Description.Basic -> getDescriptionSimple(price, unit)
        }
    }
}

private fun getDescriptionDiscounted(finalPrice: Int, discount: Float, unit: String, context: Context) : Spannable {
    val text = SpannableStringBuilder()
    text.append(
            getPriceBeforeDiscount(finalPrice, discount).addThousandSeparator() + " ",
            StrikethroughSpan(),
            Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
    text.appendMultipleSpans(
            "$" + finalPrice.addThousandSeparator(),
            listOf(StyleSpan(Typeface.BOLD), ForegroundColorSpan(context.getResourceFromAttr(R.attr.colorSecondary))),
            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
    )
    text.append(" /${abbreviatedUnit(unit)}")
    return text
}

private fun getDescriptionSimple(price: Int, unit: String) : Spannable {
    val text = SpannableStringBuilder()
    text.append(
        "$" + price.addThousandSeparator(),
        StyleSpan(Typeface.BOLD),
        Spannable.SPAN_INCLUSIVE_EXCLUSIVE
    )
    text.append(" /${abbreviatedUnit(unit)}")
    return text
}
