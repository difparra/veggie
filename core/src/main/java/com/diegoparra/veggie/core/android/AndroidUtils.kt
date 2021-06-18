package com.diegoparra.veggie.core.android

import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.widget.TextView
import timber.log.Timber

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


/**
 * Append text with multiple spans defined on it.
 */
fun SpannableStringBuilder.appendMultipleSpans(
    text: CharSequence,
    what: List<Any>,
    flags: Int
): SpannableStringBuilder {
    if (what.isNullOrEmpty()) return this
    val start = this.length
    this.append(text)
    what.forEach { style ->
        setSpan(style, start, this.length, flags)
    }
    return this
}