package com.diegoparra.veggie.core.android

import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.TextView
import com.diegoparra.veggie.core.kotlin.Resource
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
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

/**
 * Display or blank error depending on resource type (success or error)
 */
fun TextInputLayout.setErrorMessageFromResource(resource: Resource<*>){
    error = when (resource) {
        is Resource.Success -> null
        is Resource.Error -> resource.failure.getContextMessage(this.context)
        else -> null
    }
}

/**
 * Call btnContinue listener when done is pressed on this TextInputEditText
 */
fun TextInputEditText.setOnEnterListener(btnContinue: Button) {
    this.setOnEditorActionListener { v, actionId, event ->
        if(actionId == EditorInfo.IME_ACTION_DONE) {
            btnContinue.callOnClick()
            return@setOnEditorActionListener true
        }
        false
    }
}