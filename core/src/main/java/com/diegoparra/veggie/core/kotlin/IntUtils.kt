package com.diegoparra.veggie.core.kotlin

/**
 * Round number to the next multiple of ...
 * Example: 1548.roundToMultiple(10) is rounded to 1550 in default and ceil behaviour, or 1540 in floor behaviour
 * Default: 38 / 11 -> rounded to 33; 39 / 11 -> rounded to 44
 *          1545 / 10 -> rounded to 1550
 */
fun Int.roundToMultiple(
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

enum class RoundBehaviour {
    DEFAULT, CEIL, FLOOR
}

/**
 * Get the original value before the percent was applied.
 * Example Int = 90, Percent = 10, originalValue = 100 (100-10% = 90)
 */
fun Int.getValueBeforePercentApplied(percent: Float): Int {
    if (percent < 0f || percent > 1.0f) {
        throw IllegalArgumentException("Percent must be a value between 0 and 1")
    }
    if (percent == 1.0f) {
        return 0
    }
    return (this / (1 - percent)).toInt()
}