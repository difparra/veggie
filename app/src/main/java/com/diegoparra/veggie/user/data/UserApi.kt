package com.diegoparra.veggie.user.data

import android.net.Uri
import androidx.datastore.preferences.core.intPreferencesKey
import com.diegoparra.veggie.core.*
import com.diegoparra.veggie.user.entities_and_repo.UserConstants.SignInFields.EMAIL
import com.diegoparra.veggie.user.entities_and_repo.UserConstants.SignInFields.PASSWORD
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.userProfileChangeRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

class UserApi @Inject constructor(
    private val auth: FirebaseAuth
) {

    /*  Exceptions:
        https://firebase.google.com/docs/reference/kotlin/com/google/firebase/auth/FirebaseAuth?hl=es-419#createuserwithemailandpassword
     */

    fun getCurrentUserAsFlow(): Flow<FirebaseUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener {
            offer(it.currentUser)
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    suspend fun getSignInMethodsForEmail(email: String): Either<Failure, List<String>> {
        return try {
            val result = auth.fetchSignInMethodsForEmail(email).await()
            Timber.d("email: $email - signInMethods = ${result.signInMethods}")
            Either.Right(result.signInMethods ?: listOf())
        } catch (e1: FirebaseAuthInvalidCredentialsException) {
            Either.Left(SignInFailure.WrongInput.Incorrect(field = EMAIL, input = email))
        } catch (e: Exception) {
            Either.Left(Failure.ServerError(e))
        }
    }

    fun signOut() {
        auth.signOut()
    }

    suspend fun createUserWithEmailAndPassword(
        email: String, password: String
    ): Either<Failure, Unit> {
        return try {
            auth.createUserWithEmailAndPassword(email, password).await()
            Either.Right(Unit)
        } catch (e1: FirebaseAuthWeakPasswordException) {
            //  Password is not strong enough
            Either.Left(
                SignInFailure.WrongInput.Unknown(
                    field = PASSWORD,
                    input = password,
                    message = e1.localizedMessage ?: "Password is weak"
                )
            )
        } catch (e2: FirebaseAuthInvalidCredentialsException) {
            //  Email address is malformed
            Either.Left(SignInFailure.WrongInput.Incorrect(field = EMAIL, input = email))
        } catch (e3: FirebaseAuthUserCollisionException) {
            //  Already exists an account with the given email address
            Either.Left(
                SignInFailure.WrongSignInMethod.Unknown(
                    email = email,
                    message = e3.localizedMessage ?: "User Collision Exception"
                )
            )
        } catch (e: Exception) {
            Either.Left(Failure.ServerError(e))
        }
    }

    suspend fun updateProfile(
        name: String? = null, photoUrl: String? = null
    ): Either<Failure, Unit> {
        if (name == null && photoUrl == null) {
            Timber.w("Nothing updated. Name and photoUrl are null.")
            return Either.Right(Unit)
        }
        return try {
            auth.currentUser
                ?.updateProfile(userProfileChangeRequest {
                    name?.let { displayName = name }
                    photoUrl?.let { photoUri = Uri.parse(photoUrl) }
                })?.await()
            Either.Right(Unit)
        } catch (e1: FirebaseAuthInvalidUserException) {
            //  Email does not exists or has been disabled
            Either.Left(Failure.ServerError(e1))
        } catch (e: Exception) {
            Either.Left(Failure.ServerError(e))
        }
    }

    suspend fun signInWithEmailAndPassword(email: String, password: String): Either<Failure, Unit> {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            Either.Right(Unit)
        } catch (e1: FirebaseAuthInvalidUserException) {
            //  Email does not exists or has been disabled
            Either.Left(
                SignInFailure.WrongInput.Unknown(
                    field = EMAIL,
                    input = email,
                    message = e1.localizedMessage ?: "Email does not exists or has been disabled."
                )
            )
        } catch (e2: FirebaseAuthInvalidCredentialsException) {
            //  Password is wrong
            Either.Left(SignInFailure.WrongInput.Incorrect(field = PASSWORD, input = password))
        } catch (e: Exception) {
            Either.Left(Failure.ServerError(e))
        }
    }

    suspend fun resetPassword(email: String): Either<Failure, Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Either.Right(Unit)
        } catch (e: Exception) {
            Either.Left(Failure.ServerError(e))
        }
    }

}