package com.diegoparra.veggie.core.android

import android.content.Context
import android.graphics.Color
import android.util.DisplayMetrics
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import androidx.core.graphics.ColorUtils
import kotlin.math.roundToInt

fun Context.getResourcesFromAttr(
        @AttrRes attrRes: Int,
        typedValue: TypedValue = TypedValue(),
        resolveRefs: Boolean = true
) : Int {
    theme.resolveAttribute(attrRes, typedValue, resolveRefs)
    return typedValue.data
}


fun Context.getDimensionFromAttr(
    @AttrRes attrRes: Int, typedValue: TypedValue = TypedValue(), resolveRefs: Boolean = true
) : Int {
    theme.resolveAttribute(attrRes, typedValue, resolveRefs)
    return typedValue.getDimension(resources.displayMetrics).toInt()
}

fun Context.getFloatFromAttr(
        @AttrRes attrRes: Int, typedValue: TypedValue = TypedValue(), resolveRefs: Boolean = true
) : Float {
    theme.resolveAttribute(attrRes, typedValue, resolveRefs)
    return typedValue.float
}


@ColorInt
fun Context.getColorFromAttr(
    @AttrRes colorAttr: Int, typedValue: TypedValue = TypedValue(), resolveRefs: Boolean = true
) : Int = getResourcesFromAttr(colorAttr, typedValue, resolveRefs)

@ColorInt
fun Context.getColorWithAlphaFromAttrs(
        @AttrRes colorAttr : Int,
        @AttrRes alphaAttr : Int? = null,
        @FloatRange(from = 0.0, to = 1.0) alphaVal: Float? = null,
) : Int {
    val color = getColorFromAttr(colorAttr)
    val alpha = alphaVal
            ?: if(alphaAttr != null){
                getFloatFromAttr(alphaAttr)
            }else{
                0f
            }
    return colorWithAlpha(color, alpha)
}

@ColorInt
private fun colorWithAlpha(@ColorInt color: Int, alphaFactor: Float) : Int {
    if(alphaFactor == 0f){
        return color
    }
    return ColorUtils.setAlphaComponent(color, (Color.alpha(color) * alphaFactor).roundToInt())
    /*val alpha = (Color.alpha(color) * alphaFactor).roundToInt()
    val red = Color.red(color)
    val green = Color.green(color)
    val blue = Color.blue(color)
    return Color.argb(alpha, red, green, blue)*/
}
