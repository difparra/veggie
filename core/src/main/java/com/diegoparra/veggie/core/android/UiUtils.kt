package com.diegoparra.veggie.core.android

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.core.content.res.ResourcesCompat
import timber.log.Timber

fun View.hideKeyboard() {
    try {
        this.clearFocus()
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    } catch (throwable: Throwable) {
    }
}

fun View.setBackground(@DrawableRes drawableRes: Int?) {
    if (drawableRes == null) {
        background = null
        return
    }
    val drawable = ResourcesCompat.getDrawable(context.resources, drawableRes, null)
    background = drawable
}


/**
 * Call the content of the callback, just when the text has actually changed
 * This is because when device is rotated textChangedListeners trigger again, and this is not the best
 * when searching for example, as would think search query has changed and query the database again.
 */
inline fun TextView.addTextChangedListenerDistinctChanged(
    crossinline afterTextChanged: (text: Editable?) -> Unit = {}
): TextWatcher {
    val textWatcher = object : TextWatcher {
        private var initialText: String? = null
        private var newText: String? = null

        override fun beforeTextChanged(text: CharSequence?, start: Int, count: Int, after: Int) {
            initialText = text.toString()   //  This create a different reference, when using just text
                                            //  the reference is the same, so when comparing initialText
                                            //  and newText they will be always the same
        }

        override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
            newText = text.toString()
        }

        override fun afterTextChanged(s: Editable?) {
            if (initialText != newText && initialText != null) {
                afterTextChanged.invoke(s)
            } else {
                Timber.e("initialText = $initialText and newText = $newText are the same. After textChanged function Callback not called.")
            }
        }
    }
    addTextChangedListener(textWatcher)

    return textWatcher
}