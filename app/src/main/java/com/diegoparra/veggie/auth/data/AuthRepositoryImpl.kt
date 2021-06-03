package com.diegoparra.veggie.auth.data

import com.diegoparra.veggie.core.*
import com.diegoparra.veggie.products.IoDispatcher
import com.diegoparra.veggie.auth.data.utils.CredentialToFirebaseAuthTransformations.getAuthCredential
import com.diegoparra.veggie.auth.data.AuthTransformations.toProfile
import com.diegoparra.veggie.auth.data.AuthTransformations.isSignedIn
import com.diegoparra.veggie.auth.data.firebase.ProfileInfoUpdateFirebase
import com.diegoparra.veggie.auth.data.firebase.AuthApi
import com.diegoparra.veggie.auth.data.prefs.AuthPrefs
import com.diegoparra.veggie.auth.domain.Profile
import com.diegoparra.veggie.auth.domain.SignInMethod
import com.diegoparra.veggie.auth.domain.AuthRepository
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApi,
    private val authPrefs: AuthPrefs,
    private val googleSignInClient: GoogleSignInClient,
    private val loginManager: LoginManager,
    @IoDispatcher private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : AuthRepository {


    //      ----------      BASIC OPERATIONS        ------------------------------------------------

    override fun isSignedIn(): Flow<Boolean> {
        return authApi
            .getCurrentUserAsFlow()
            .map { it.isSignedIn() }
            .flowOn(dispatcher)
    }

    override suspend fun signOut() {
        Timber.d("sign out")
        authApi.signOut()
        if (authPrefs.getLastSignedInWith() == SignInMethod.FACEBOOK) {
            Timber.d("sign out with facebook")
            loginManager.logOut()
        }
        if (authPrefs.getLastSignedInWith() == SignInMethod.GOOGLE) {
            Timber.d("sign out with google")
            googleSignInClient.signOut()
        }
    }


    //      ----------      BASIC INFORMATION        -----------------------------------------------

    override fun getProfile(): Flow<Either<Failure, Profile>> {
        return authApi
            .getCurrentUserAsFlow()
            .map {
                it.toProfile(
                    authPrefs.getLastSignedInWith(),
                    authPrefs.getFacebookAccessToken()
                )
            }
            .flowOn(dispatcher)
    }

    override suspend fun getSignInMethodsForEmail(email: String): Either<Failure, List<SignInMethod>> =
        withContext(dispatcher) {
            authApi.getSignInMethodsForEmail(email)
        }


    //      ----------      SIGNIN/UP EMAIL        -------------------------------------------------

    override suspend fun signUpWithEmailAndPassword(
        profile: Profile, password: String
    ): Either<Failure, Unit> = withContext(dispatcher) {
        authApi
            .createUserWithEmailAndPassword(profile.email, password)
            .suspendFlatMap {
                authApi.updateProfile(ProfileInfoUpdateFirebase(profile.name, profile.photoUrl))
            }
            .savePrefsSignedInWith(SignInMethod.EMAIL)
    }

    override suspend fun signInWithEmailAndPassword(
        email: String, password: String
    ): Either<Failure, Unit> = withContext(dispatcher) {
        authApi
            .signInWithEmailAndPassword(email, password)
            .savePrefsSignedInWith(SignInMethod.EMAIL)
    }

    override suspend fun sendPasswordResetEmail(email: String): Either<Failure, Unit> =
        withContext(dispatcher) {
            authApi.resetPassword(email)
        }


    //      ----------      SIGNIN/UP GOOGLE & FACEBOOK        -------------------------------------

    override suspend fun signInWithGoogleAccount(account: GoogleSignInAccount): Either<Failure, Unit> {
        val credential = account.getAuthCredential()
        return authApi
            .signInWithCredential(credential)
            .savePrefsSignedInWith(SignInMethod.GOOGLE)
    }

    override suspend fun signInWithFacebookResult(result: LoginResult): Either<Failure, Unit> {
        val credential = result.getAuthCredential()
        return authApi
            .signInWithCredential(credential)
            .saveFbAccessToken(result.accessToken.token)
            .savePrefsSignedInWith(SignInMethod.FACEBOOK)
    }


    //      ----------      UTIL FUNCTIONS TO SAVE PREFS     ----------------------------------------

    private suspend fun <L, R> Either<L, R>.savePrefsSignedInWith(signInMethod: SignInMethod): Either<L, R> {
        if (this is Either.Right) {
            authPrefs.saveLastSignedInWith(signInMethod)
        }
        return this
    }

    private suspend fun <L, R> Either<L, R>.saveFbAccessToken(token: String): Either<L, R> {
        if (this is Either.Right) {
            authPrefs.saveFacebookAccessToken(token)
        }
        return this
    }

}