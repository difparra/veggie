package com.diegoparra.veggie.user.data

import com.diegoparra.veggie.core.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserApi @Inject constructor(
    private val auth: FirebaseAuth
) {

    fun isSignedInAsFlow(): Flow<Boolean> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener {
            offer(isSignedIn(it.currentUser))
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    private fun isSignedIn(user: FirebaseUser?): Boolean {
        return user != null && !user.isAnonymous
    }


    suspend fun getSignInMethodsForEmail(email: String): Either<Failure, List<String>> {
        return try{
            val result = auth.fetchSignInMethodsForEmail(email).await()
            Either.Right(result.signInMethods ?: listOf())
        }catch (e: Exception){
            Either.Left(SignInFailure.FirebaseException(e))
        }
    }

}