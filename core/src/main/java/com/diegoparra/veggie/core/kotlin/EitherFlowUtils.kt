package com.diegoparra.veggie.core.kotlin

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/*
 *  Short hand functions to return either "wrapped" in a flow when necessary.
 *  The flow created will emit just once.
 */

fun <L, R> createSingleFlowEither(either: Either<L,R>): Flow<Either<L, R>> {
    return flow { emit(either) }
}

fun <L: Failure, R> createSingleFlowEither(failure: L): Flow<Either<L, R>> {
    return flow { emit(Either.Left(failure)) }
}

fun <L: Failure, R> createSingleFlowEither(success: R): Flow<Either<L, R>> {
    return flow { emit(Either.Right(success)) }
}