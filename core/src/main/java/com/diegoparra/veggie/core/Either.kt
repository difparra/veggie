package com.diegoparra.veggie.core

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
    /** * Represents the left side of [Either] class which by convention is a "Failure". */
    data class Left<out L>(val a: L) : Either<L, Nothing>()

    /** * Represents the right side of [Either] class which by convention is a "Success". */
    data class Right<out R>(val b: R) : Either<Nothing, R>()

    /**
     * Returns true if this is a Right, false otherwise.
     * @see Right
     */
    val isRight get() = this is Right<R>

    /**
     * Returns true if this is a Left, false otherwise.
     * @see Left
     */
    val isLeft get() = this is Left<L>

    /**
     * Creates a Left type.
     * @see Left
     */
    fun <L> left(a: L) = Left(a)


    /**
     * Creates a Right type.
     * @see Right
     */
    fun <R> right(b: R) = Right(b)

    /**
     * Applies fnL if this is a Left or fnR if this is a Right.
     * @see Left
     * @see Right
     */
    fun fold(fnL: (L) -> Any, fnR: (R) -> Any): Any =
        when (this) {
            is Left -> fnL(a)
            is Right -> fnR(b)
        }
}

/**
 * Composes 2 functions
 * See <a href="https://proandroiddev.com/kotlins-nothing-type-946de7d464fb">Credits to Alex Hart.</a>
 */
fun <A, B, C> ((A) -> B).c(f: (B) -> C): (A) -> C = {
    f(this(it))
}
/*
    A -> B      =>      b = a2                      b = f(a)            T = fn(R)
    B -> C      =>      c = b3                      c = f(b)            Either = Right(T)
    A -> C      =>      c = b3 = (a2)3 = a6     =>  c = f(f(a))         Either = Right(fn(R))
 */

/**
 * Right-biased flatMap() FP convention which means that Right is assumed to be the default case
 * to operate on. If it is Left, operations like map, flatMap, ... return the Left value unchanged.
 */
fun <T, L, R> Either<L, R>.flatMap(fn: (R) -> Either<L, T>): Either<L, T> =
    when (this) {
        is Either.Left -> Either.Left(a)
        is Either.Right -> fn(b)
    }

/**
 * Right-biased map() FP convention which means that Right is assumed to be the default case
 * to operate on. If it is Left, operations like map, flatMap, ... return the Left value unchanged.
 */
fun <T, L, R> Either<L, R>.map(fn: (R) -> (T)): Either<L, T> = this.flatMap(fn.c(::right))
//  fn.c(::right)       =>      R->T , T->Either.Right(T)  =>  R -> Either.Right(T)

/** Returns the value from this `Right` or the given argument if this is a `Left`.
 *  Right(12).getOrElse(17) RETURNS 12 and Left(12).getOrElse(17) RETURNS 17
 */
fun <L, R> Either<L, R>.getOrElse(value: R): R =
    when (this) {
        is Either.Left -> value
        is Either.Right -> b
    }

/**
 * Left-biased onFailure() FP convention dictates that when this class is Left, it'll perform
 * the onFailure functionality passed as a parameter, but, overall will still return an either
 * object so you chain calls.
 */
fun <L, R> Either<L, R>.onFailure(fn: (failure: L) -> Unit): Either<L, R> =
    this.apply { if (this is Either.Left) fn(a) }

/**
 * Right-biased onSuccess() FP convention dictates that when this class is Right, it'll perform
 * the onSuccess functionality passed as a parameter, but, overall will still return an either
 * object so you chain calls.
 */
fun <L, R> Either<L, R>.onSuccess(fn: (success: R) -> Unit): Either<L, R> =
    this.apply { if (this is Either.Right) fn(b) }


/*
    ------------------------------------------------------------------------------------------------
        CUSTOM FUNCTIONS WITH EITHER
    ------------------------------------------------------------------------------------------------
 */

/**
 * Transform a List of Either<> into a Either<_,List>. (Left-Biased)
 * The returned value will be:
 *  - Failure:  If any of the elements in the list is Failure (Left)
 *  - Success:  If every item in the list is Success (Right)
 */
fun <L,R> List<Either<L, R>>.mapList() : Either<List<L>, List<R>> {
    val failures = this.filter { it is Either.Left }
    return if(failures.isNotEmpty()){
        Either.Left(failures.map { (it as Either.Left).a })
    }else{
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
fun <L,R> List<Either<L, R>>.mapListAndFlattenFailure(flattenFailure: (List<L>)->L = { it.first() }) : Either<L, List<R>> {
    return when(val mappedList = this.mapList()){
        is Either.Left -> Either.Left(flattenFailure(mappedList.a))
        is Either.Right -> mappedList
    }
}


/**
 * Return the failures contained in a list of Either
 */
fun <L> List<Either<L, Any>>.getFailures() : List<L> {
    return this
        .filter { it is Either.Left }
        .map {
            (it as Either.Left).a
        }
}


/**
 * Transform Either<Failure, T> into Resource.Success or Resource.Error
 */
fun <T> Either<Failure, T>.toResource() : Resource<T> {
    return when(this){
        is Either.Left -> Resource.Error(this.a)
        is Either.Right -> Resource.Success(this.b)
    }
}



/**
 * Right-biased customMap()
 * Convert an Either when the Right parameter is a List.
 * The returned value will be:
 *  - Failure: If the either was initially a failure
 *  - Failure: If for any of the elements in the list the transformation (parameter) results in failure.
 *  - Success: When list transformed does not contain any failure.
 *//*

fun <T, L, R> Either<L, List<R>>.customMap(fn: (R) -> Either<L, T>): Either<L, List<T>> =
    this.flatMap { it.map(fn).mapListAndFlattenFailure() }

*/



suspend fun <T,L,R> Either<L,R>.suspendMap(fn: suspend (R) -> T) : Either<L,T> {
    return when(this){
        is Either.Left -> this
        is Either.Right -> Either.Right(fn(this.b))
    }
}

suspend fun <T,L,R> Either<L,R>.suspendFlatMap(fn: suspend (R) -> Either<L,T>) : Either<L,T> {
    return when(this){
        is Either.Left -> this
        is Either.Right -> fn(this.b)
    }
}