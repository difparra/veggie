package com.diegoparra.veggie.products.ui

import android.content.Context
import android.content.res.ColorStateList
import android.util.TypedValue
import androidx.annotation.AttrRes
import com.diegoparra.veggie.R
import com.diegoparra.veggie.core.Constants
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

fun getWeightString(weightGr: Int) : String? =
    if(weightGr == Constants.Products.NoWeightGr) null
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

fun Context.getLabel(stock: Boolean, discount: Float, suggestedLabel: String) : Pair<String, ColorStateList>? {
    /*val colorNoStock = ResourcesCompat.getColor(resources, R.color.black_light, theme)
    val colorDiscount = ResourcesCompat.getColor(resources, R.color.red_700, theme)
    val additionalColors = resources.getIntArray(R.array.dark_colors)*/
    val colorNoStock = getResourceFromAttr(R.attr.colorControlNormal)
    val colorDiscount = getResourceFromAttr(R.attr.colorSecondary)
    val additionalColors = resources.getIntArray(getResourceFromAttr(R.attr.colorsRandom))

    return if(!stock){
        Pair(first = "Agotado", second = ColorStateList.valueOf(colorNoStock))
    }else if(discount > 0){
        Pair(first = "${(discount*100).toInt()}% Off", second = ColorStateList.valueOf(colorDiscount))
    }else if(suggestedLabel != Constants.Products.NoLabel){
        val color = when(suggestedLabel.toLowerCase(Locale.ROOT)){
            "recomendado" -> additionalColors[0]
            "popular" -> additionalColors.getOrElse(1) { additionalColors[0] }
            else -> additionalColors.getOrElse(2) { additionalColors[0] }
        }
        Pair(first = suggestedLabel, second = ColorStateList.valueOf(color))
    }else{
        null
    }
}