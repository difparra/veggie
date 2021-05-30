package com.diegoparra.veggie.core

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.snackbar.Snackbar

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