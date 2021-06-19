package com.diegoparra.veggie.auth.domain

import android.net.Uri
import com.diegoparra.veggie.auth.utils.AuthFailure
import com.diegoparra.veggie.core.kotlin.Either
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.PhoneAuthCredential
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    fun isSignedIn(): Flow<Boolean>
    suspend fun signOut()

    suspend fun getIdCurrentUser(): Either<AuthFailure, String>
    fun getIdCurrentUserAsFlow(): Flow<Either<AuthFailure, String>>
    suspend fun getProfile(): Either<AuthFailure, Profile>
    fun getProfileAsFlow(): Flow<Either<AuthFailure, Profile>>
    suspend fun getSignInMethodsForEmail(email: String): Either<AuthFailure, List<SignInMethod>>

    suspend fun updateProfile(name: String? = null, photoUrl: Uri? = null): Either<AuthFailure, Unit>
    suspend fun updatePhoneNumber(credential: PhoneAuthCredential): Either<AuthFailure, Unit>

    suspend fun signUpWithEmailAndPassword(
        profile: Profile,
        password: String
    ): Either<AuthFailure, AuthResults>

    suspend fun signInWithEmailAndPassword(
        email: String,
        password: String
    ): Either<AuthFailure, AuthResults>

    suspend fun sendPasswordResetEmail(email: String): Either<AuthFailure, Unit>

    suspend fun signInWithGoogleAccount(account: GoogleSignInAccount): Either<AuthFailure, AuthResults>
    suspend fun signInWithFacebookResult(result: LoginResult): Either<AuthFailure, AuthResults>

}