package com.diegoparra.veggie.auth.domain

import android.net.Uri
import com.diegoparra.veggie.auth.utils.AuthFailure
import com.diegoparra.veggie.core.kotlin.Either
import com.diegoparra.veggie.core.kotlin.Failure
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.PhoneAuthCredential
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    fun isSignedIn(): Flow<Boolean>
    suspend fun signOut()

    suspend fun getIdCurrentUser(): Either<Failure, String>
    fun getIdCurrentUserAsFlow(): Flow<Either<Failure, String>>
    suspend fun getProfile(): Either<Failure, Profile>
    fun getProfileAsFlow(): Flow<Either<Failure, Profile>>
    suspend fun getSignInMethodsForEmail(email: String): Either<Failure, List<SignInMethod>>

    suspend fun updateProfile(name: String? = null, photoUrl: Uri? = null): Either<Failure, Unit>
    suspend fun updatePhoneNumber(credential: PhoneAuthCredential): Either<Failure, Unit>

    suspend fun signUpWithEmailAndPassword(
        profile: Profile,
        password: String
    ): Either<Failure, AuthResults>

    suspend fun signInWithEmailAndPassword(
        email: String,
        password: String
    ): Either<Failure, AuthResults>

    suspend fun sendPasswordResetEmail(email: String): Either<Failure, Unit>

    suspend fun signInWithGoogleAccount(account: GoogleSignInAccount): Either<Failure, AuthResults>
    suspend fun signInWithFacebookResult(result: LoginResult): Either<Failure, AuthResults>

}