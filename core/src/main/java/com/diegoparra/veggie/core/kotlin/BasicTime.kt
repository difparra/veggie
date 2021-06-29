package com.diegoparra.veggie.core.kotlin

/**
 * It is a really simple class, just to hold millis from Epoch UTC.
 * It is useful when just some simple comparisons around time are needed. For example,
 * calling update if time has expired.
 *
 * Benefits:
 * - It avoids creating objects as it is an inlined class (compared to Date, that is also currently
 *   deprecated).
 * - It does not require neither dependencies (Timestamp - Firebase) nor desugaring (LocalDateTime).
 *
 * Warnings:
 * - As long as this is a value (inline) class:
 *      Don't use directly in databases (Firestore and room), because at it is an inline class,
 *      it has not normal getter and setter methods necessary in room and Firebase.
 *      Therefore, when working with databases, use directly the wrapped value.
 */
@JvmInline
value class BasicTime(val millisEpochUTC: Long) {
    companion object {
        fun now() = BasicTime(System.currentTimeMillis())
        fun create(seconds: Long, nanoseconds: Int) =
            BasicTime(seconds * 1000 + (nanoseconds / 1000000))
    }

    fun isBefore(time: BasicTime): Boolean =
        millisEpochUTC < time.millisEpochUTC

    fun isAfter(time: BasicTime): Boolean =
        millisEpochUTC > time.millisEpochUTC

}