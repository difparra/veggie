package com.diegoparra.veggie.core.kotlin

/**
 * Represents a value of one of two possible types (a disjoint union).
 * Instances of [Either] are either an instance of [Left] or [Right].
 * FP Convention dictates that [Left] is used for "failure"
 * and [Right] is used for "success".
 *
 * @see Left
 * @see Right
 */
sealed class Either<out L, out R> {
    //  In order to define static functions
    companion object {}

    /** * Represents the left side of [Either] class which by convention is a "Failure". */
    data class Left<out L>(val a: L) : Either<L, Nothing>()

    /** * Represents the right side of [Either] class which by convention is a "Success". */
    data class Right<out R>(val b: R) : Either<Nothing, R>()

    /**
     * Applies fnL if this is a Left or fnR if this is a Right.
     * @see Left
     * @see Right
     */
    inline fun <T> fold(fnL: (L) -> T, fnR: (R) -> T): T =
        when (this) {
            is Left -> fnL(a)
            is Right -> fnR(b)
        }
}

fun <L, R> Either<L, R>.getOrNull(): R? =
    when (this) {
        is Either.Left -> null
        is Either.Right -> b
    }

fun <L, R> Either<L, R>.leftOrNull(): L? =
    when (this) {
        is Either.Left -> a
        is Either.Right -> null
    }


inline fun <L, R> Either<L, R>.getOrElse(onFailure: (failure: L) -> R): R =
    when (this) {
        is Either.Left -> onFailure(a)
        is Either.Right -> b
    }

inline fun <L, R> Either<L, R>.onFailure(fn: (failure: L) -> Unit): Either<L, R> =
    this.apply { if (this is Either.Left) fn(a) }

inline fun <L, R> Either<L, R>.onSuccess(fn: (success: R) -> Unit): Either<L, R> =
    this.apply { if (this is Either.Right) fn(b) }


/*
    ------------------------------------------------------------------------------------------------
        TRANSFORMATIONS
    ------------------------------------------------------------------------------------------------
 */
/**
 * Right-biased flatMap() FP convention which means that Right is assumed to be the default case
 * to operate on. If it is Left, operations like map, flatMap, ... return the Left value unchanged.
 */
inline fun <T, L, R> Either<L, R>.flatMap(fn: (R) -> Either<L, T>): Either<L, T> =
    when (this) {
        is Either.Left -> this
        is Either.Right -> fn(b)
    }

/**
 * Right-biased map() FP convention which means that Right is assumed to be the default case
 * to operate on. If it is Left, operations like map, flatMap, ... return the Left value unchanged.
 */
inline fun <T, L, R> Either<L, R>.map(fn: (R) -> (T)): Either<L, T> =
    when (this) {
        is Either.Left -> this
        is Either.Right -> Either.Right(fn(b))
    }


/*
    ------------------------------------------------------------------------------------------------
        CUSTOM FUNCTIONS WITH LIST OF EITHER
    ------------------------------------------------------------------------------------------------
 */


/**
 * Transform a List of Either<> into a Either<_,List>. (Left-Biased)
 * The returned value will be:
 *  - Failure:  If any of the elements in the list is Failure (Left)
 *  - Success:  If every item in the list is Success (Right)
 */
fun <L, R> List<Either<L, R>>.getFailuresOrRight(): Either<List<L>, List<R>> {
    val failures = this.filterIsInstance<Either.Left<L>>().map { it.a }
    return if (failures.isNotEmpty()) {
        Either.Left(failures)
    } else {
        Either.Right(this.map { (it as Either.Right).b })
    }
}

/**
 * Transform a List of Either<> into a Either<_,List>. (Left-Biased)
 * The returned value will be:
 *  - Failure:  If any of the elements in the list is Failure(Left).
 *              Returned Failure will be the first Failure encountered in the list.
 *  - Success:  If every item in the list is Success (Right).
 *              Returned Success will now represent the list<R> without failures.
 */
inline fun <L, R> List<Either<L, R>>.reduceFailuresOrRight(reduceFailure: (List<L>) -> L = { it.first() }): Either<L, List<R>> {
    return when (val mappedList = this.getFailuresOrRight()) {
        is Either.Left -> Either.Left(reduceFailure(mappedList.a))
        is Either.Right -> mappedList
    }
}


/**
 * Return the failures contained in a list of Either
 */
fun <L> List<Either<L, Any>>.getFailures(): List<L> {
    return this
        .filterIsInstance<Either.Left<L>>()
        .map { it.a }
}


/*
    ------------------------------------------------------------------------------------------------
        TRANSFORM TO RESOURCE
    ------------------------------------------------------------------------------------------------
 */

fun <T> Either<Failure, T>.toResource(): Resource<T> {
    return when (this) {
        is Either.Left -> Resource.Error(this.a)
        is Either.Right -> Resource.Success(this.b)
    }
}



/*
    ------------------------------------------------------------------------------------------------
        CHAIN AND COMBINE MAPS
    ------------------------------------------------------------------------------------------------
 */

inline fun <T1, T2, R> Either.Companion.combineMap(
    either1: Either<Failure, T1>,
    either2: Either<Failure, T2>,
    transform: (T1, T2) -> R
): Either<Failure, R> {
    return when (either1) {
        is Either.Left -> either1
        is Either.Right -> {
            when (either2) {
                is Either.Left -> either2
                is Either.Right -> Either.Right(transform(either1.b, either2.b))
            }
        }
    }
}

inline fun <T1, T2, T3, R> Either.Companion.combineMap(
    either1: Either<Failure, T1>,
    either2: Either<Failure, T2>,
    either3: Either<Failure, T3>,
    transform: (T1, T2, T3) -> R
): Either<Failure, R> {
    return when (either1) {
        is Either.Left -> return either1
        is Either.Right -> {
            when (either2) {
                is Either.Left -> either2
                is Either.Right -> {
                    when (either3) {
                        is Either.Left -> either3
                        is Either.Right -> Either.Right(
                            transform(
                                either1.b,
                                either2.b,
                                either3.b
                            )
                        )
                    }
                }
            }
        }
    }
}