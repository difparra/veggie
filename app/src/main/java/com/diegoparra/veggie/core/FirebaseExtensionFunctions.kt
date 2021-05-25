package com.diegoparra.veggie.core

/**
 * Waits for the result of a FirebaseTask and send Either to wrapper coroutine/suspend function
 * Were working fine, but there is a dependency implementation "org.jetbrains.kotlinx:kotlinx-coroutines-play-services:$firebaseCoroutines"
 * already with classes to manage this case, and is reliable as it is from google.
 * In addition, adding the dependency this class does not need to be repeated on every module
 * implementing firebase, as it wasn't placed in core module to keep it as a simple java/kotlin module
 */
/*internal suspend fun <T, R> Task<T>.awaitEither(
    onSuccessListener: (T) -> Either<Failure, R>,
    onFailureListener: (Exception) -> Either<Failure, R> = { Either.Left(Failure.FirebaseException(it)) }
): Either<Failure, R> = suspendCoroutine { continuation ->
    this
        .addOnSuccessListener {
            continuation.resume(onSuccessListener(it))
        }
        .addOnFailureListener {
            continuation.resume(onFailureListener(it))
        }
        .addOnCanceledListener {
            continuation.resume(Either.Left(Failure.ServerError(exception = null, message = "$this Firebase Task was cancelled")))
        }
}*/