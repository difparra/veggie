package com.diegoparra.veggie.auth.domain

import android.net.Uri
import com.diegoparra.veggie.core.Either
import com.diegoparra.veggie.core.Failure
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    fun isSignedIn(): Flow<Boolean>
    suspend fun signOut()

    suspend fun getIdCurrentUser(): Either<Failure, String>
    suspend fun getProfile(): Either<Failure, Profile>
    fun getProfileAsFlow(): Flow<Either<Failure, Profile>>

    suspend fun updateProfile(name: String? = null, photoUrl: Uri? = null): Either<Failure, Unit>
    suspend fun getSignInMethodsForEmail(email: String): Either<Failure, List<SignInMethod>>

    suspend fun signUpWithEmailAndPassword(profile: Profile, password: String): Either<Failure, Profile>
    suspend fun signInWithEmailAndPassword(email: String, password: String): Either<Failure, Profile>
    suspend fun sendPasswordResetEmail(email: String): Either<Failure, Unit>

    suspend fun signInWithGoogleAccount(account: GoogleSignInAccount): Either<Failure, Profile>
    suspend fun signInWithFacebookResult(result: LoginResult): Either<Failure, Profile>

}