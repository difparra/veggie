package com.diegoparra.veggie.auth.data.firebase

import android.net.Uri
import com.diegoparra.veggie.auth.data.firebase.FirebaseAuthExceptionsTransformations.toFailure
import com.diegoparra.veggie.auth.data.AuthTransformations.fromSignInMethod
import com.diegoparra.veggie.auth.utils.AuthFailure
import com.diegoparra.veggie.auth.domain.SignInMethod
import com.diegoparra.veggie.core.kotlin.Either
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.userProfileChangeRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

class AuthApi @Inject constructor(
    private val auth: FirebaseAuth
) {

    /*  Exceptions:
        https://firebase.google.com/docs/reference/kotlin/com/google/firebase/auth/FirebaseAuth?hl=es-419#createuserwithemailandpassword
     */


    //      ----------      BASIC OPERATIONS        ------------------------------------------------

    fun getCurrentUser(): FirebaseUser? = auth.currentUser

    fun getCurrentUserAsFlow(): Flow<FirebaseUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener {
            offer(it.currentUser)
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    fun signOut() {
        auth.signOut()
    }

    suspend fun updateProfile(
        name: String? = null, photoUrl: Uri? = null
    ): Either<AuthFailure, Unit> {
        if (name == null && photoUrl == null) {
            Timber.w("Nothing updated. Name and photoUrl are null or forceUpdates were false.")
            return Either.Right(Unit)
        }
        return try {
            auth.currentUser
                ?.updateProfile(userProfileChangeRequest {
                    name?.let {
                        Timber.d("updating name to = $it")
                        displayName = it
                    }
                    photoUrl?.let {
                        Timber.d("updating photoUrl to = $it")
                        photoUri = it
                    }
                })?.await()
            Either.Right(Unit)
        } catch (e1: FirebaseAuthInvalidUserException) {
            //  Email does not exists or has been disabled
            Either.Left(e1.toFailure(null))
        } catch (e: Exception) {
            Either.Left(AuthFailure.ServerError(e))
        }
    }

    suspend fun updatePhoneNumber(credential: PhoneAuthCredential): Either<AuthFailure, Unit> {
        return try {
            auth.currentUser
                ?.updatePhoneNumber(credential)
                ?.await()
            Either.Right(Unit)
        } catch (e1: FirebaseAuthInvalidCredentialsException) {
            val message = e1.message
            if (message != null) {
                if (message.contains("invalid", ignoreCase = true)) {
                    Either.Left(AuthFailure.PhoneAuthFailures.InvalidSmsCode)
                } else if (message.contains("expired", ignoreCase = true)) {
                    Either.Left(AuthFailure.PhoneAuthFailures.ExpiredSmsCode)
                } else {
                    Either.Left(AuthFailure.ServerError(e1))
                }
            } else {
                Either.Left(AuthFailure.ServerError(e1))
            }
        } catch (e: Exception) {
            Either.Left(AuthFailure.ServerError(e))
        }

    }

    suspend fun getSignInMethodsForEmail(email: String): Either<AuthFailure, List<SignInMethod>> {
        return try {
            val result = auth.fetchSignInMethodsForEmail(email).await()
            Timber.d("email: $email - signInMethods = ${result.signInMethods}")
            val signInMethodList = result.signInMethods?.map {
                SignInMethod.fromSignInMethod(it)
            }
            Either.Right(signInMethodList ?: emptyList())
        } catch (e1: FirebaseAuthInvalidCredentialsException) {
            Either.Left(e1.toFailure(email = email))
        } catch (e: Exception) {
            Either.Left(AuthFailure.ServerError(e))
        }
    }


    //      ----------      SIGNIN/UP EMAIL        -------------------------------------------------

    suspend fun createUserWithEmailAndPassword(
        email: String, password: String
    ): Either<AuthFailure, AuthResult> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            Either.Right(result)
        } catch (e1: FirebaseAuthWeakPasswordException) {
            //  Password is not strong enough
            Either.Left(e1.toFailure(password = password))
        } catch (e2: FirebaseAuthInvalidCredentialsException) {
            //  Email address is malformed
            Either.Left(e2.toFailure(email = email))
        } catch (e3: FirebaseAuthUserCollisionException) {
            //  Already exists an account with the given email address
            Either.Left(e3.toFailure(SignInMethod.EMAIL, getSignInMethodsForEmail(e3.email!!)))
        } catch (e: Exception) {
            Either.Left(AuthFailure.ServerError(e))
        }
    }

    suspend fun signInWithEmailAndPassword(
        email: String,
        password: String
    ): Either<AuthFailure, AuthResult> {
        return try {
            //  Equivalent to call signInWithCredential with an EmailAuthCredential generated by EmailAuthProvider.getCredential(String, String)
            val result = auth.signInWithEmailAndPassword(email, password).await()
            Either.Right(result)
        } catch (e1: FirebaseAuthInvalidUserException) {
            //  Email does not exists or has been disabled, or user/email does not exist
            Either.Left(e1.toFailure(email = email))
        } catch (e2: FirebaseAuthInvalidCredentialsException) {
            //  Password is incorrect
            Either.Left(e2.toFailure(password = password))
        } catch (e3: FirebaseAuthUserCollisionException) {
            //  If there already exists an account with the email address
            Either.Left(e3.toFailure(SignInMethod.EMAIL, getSignInMethodsForEmail(e3.email!!)))
        } catch (e: Exception) {
            Either.Left(AuthFailure.ServerError(e))
        }
    }

    suspend fun resetPassword(email: String): Either<AuthFailure, Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Either.Right(Unit)
        } catch (e: Exception) {
            Either.Left(AuthFailure.ServerError(e))
        }
    }


    //      ----------      SIGNIN/UP GOOGLE & FACEBOOK       --------------------------------------

    suspend fun signInWithCredential(credential: AuthCredential): Either<AuthFailure, AuthResult> {
        return try {
            val result = auth.signInWithCredential(credential).await()
            Either.Right(result)
        } catch (e1: FirebaseAuthInvalidUserException) {
            //  If the user account has been disabled or is EmailAuthCredential for non-existent user/email
            Either.Left(e1.toFailure(null))
        } catch (e2: FirebaseAuthInvalidCredentialsException) {
            //  If the credential is malformed or has been expired / Password is incorrect
            Either.Left(e2.toFailure(email = null))
        } catch (e3: FirebaseAuthUserCollisionException) {
            //  If there already exists an account with the email address asserted by the credential
            Either.Left(e3.toFailure(SignInMethod.EMAIL, getSignInMethodsForEmail(e3.email!!)))
        } catch (e: Exception) {
            Either.Left(AuthFailure.ServerError(e))
        }
    }

}