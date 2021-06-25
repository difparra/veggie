package com.diegoparra.veggie.core.kotlin

import java.text.NumberFormat
import java.util.*


/**
 * Round some number and add the currency symbols.
 */
fun Int.addPriceFormat(roundToMultiple: Int = 10): String {
    val roundedPrice = this.roundToMultiple(roundToMultiple)
    return "$" + roundedPrice.addThousandSeparator()
}

private fun Int.addThousandSeparator(): String {
    return NumberFormat.getNumberInstance(Locale.US).format(this)
}

/**
 * Round number to the next multiple of ...
 * Example: 1548.roundToMultiple(10) is rounded to 1550 in default and ceil behaviour, or 1540 in floor behaviour
 * Default: 38 / 11 -> rounded to 33; 39 / 11 -> rounded to 44
 *          1545 / 10 -> rounded to 1550
 */
private fun Int.roundToMultiple(
    multiple: Int = 10,
    behaviour: RoundBehaviour = RoundBehaviour.DEFAULT
): Int {
    if (this % multiple == 0) {
        return this
    }
    return when (behaviour) {
        RoundBehaviour.CEIL -> ((this / multiple) * multiple) + multiple
        RoundBehaviour.FLOOR -> (this / multiple) * multiple
        RoundBehaviour.DEFAULT -> {
            if (this % multiple >= (multiple / 2 + multiple % 2)) {
                ((this / multiple) * multiple) + multiple
            } else {
                (this / multiple) * multiple
            }
        }
    }
}

private enum class RoundBehaviour {
    DEFAULT, CEIL, FLOOR
}


/**
 * Get the original value before the discount was applied.
 * Example Int = 90, Percent = 10, originalValue = 100 (100-10% = 90)
 */
fun Int.getValueBeforeDiscount(discount: Float): Int {
    if (discount < 0f || discount > 1.0f) {
        throw IllegalArgumentException("Percent must be a value between 0 and 1")
    }
    if(discount == 0f) {
        return this
    }
    if (discount == 1.0f) {
        return 0
    }
    return (this / (1 - discount)).toInt()
}