package com.diegoparra.veggie.auth.domain

import com.diegoparra.veggie.core.Either
import com.diegoparra.veggie.core.Failure
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    fun isSignedIn(): Flow<Boolean>
    suspend fun signOut()

    fun getProfile(): Flow<Either<Failure, Profile>>
    suspend fun getSignInMethodsForEmail(email: String): Either<Failure, List<SignInMethod>>

    suspend fun signUpWithEmailAndPassword(user: Profile, password: String): Either<Failure, Unit>
    suspend fun signInWithEmailAndPassword(email: String, password: String): Either<Failure, Unit>
    suspend fun sendPasswordResetEmail(email: String): Either<Failure, Unit>

    suspend fun signInWithGoogleAccount(account: GoogleSignInAccount): Either<Failure, Unit>
    suspend fun signInWithFacebookResult(result: LoginResult): Either<Failure, Unit>

}