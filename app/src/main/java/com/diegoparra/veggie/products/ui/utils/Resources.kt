package com.diegoparra.veggie.products.ui.utils

import android.content.Context
import android.graphics.Color
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.FloatRange
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.ColorUtils
import kotlin.math.roundToInt


fun View.hideKeyboard() {
    try{
        this.clearFocus()
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }catch (throwable: Throwable){}
}


fun View.setBackground(@DrawableRes drawableRes: Int?) {
    if(drawableRes == null){
        background = null
        return
    }
    val drawable = ResourcesCompat.getDrawable(context.resources, drawableRes, null)
    background = drawable
}

fun Context.getResourcesFromAttr(
        @AttrRes attrRes: Int,
        typedValue: TypedValue = TypedValue(),
        resolveRefs: Boolean = true
) : Int {
    theme.resolveAttribute(attrRes, typedValue, resolveRefs)
    return typedValue.data
}


@ColorInt
fun Context.getColorFromAttr(
        @AttrRes colorAttr: Int, typedValue: TypedValue = TypedValue(), resolveRefs: Boolean = true
) : Int = getResourcesFromAttr(colorAttr, typedValue, resolveRefs)

private fun Context.getFloatFromAttr(
        @AttrRes attrRes: Int, typedValue: TypedValue = TypedValue(), resolveRefs: Boolean = true
) : Float {
    theme.resolveAttribute(attrRes, typedValue, resolveRefs)
    return typedValue.float
}


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
