package com.diegoparra.veggie.auth.data

import android.net.Uri
import com.diegoparra.veggie.core.*
import com.diegoparra.veggie.products.IoDispatcher
import com.diegoparra.veggie.auth.data.utils.CredentialToFirebaseAuthTransformations.getAuthCredential
import com.diegoparra.veggie.auth.data.AuthTransformations.isSignedIn
import com.diegoparra.veggie.auth.data.AuthTransformations.toProfile
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
        //  It is also good to get as flow, so that it can deal when user is deleted from firebase.
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
            .map { it.toProfile() }
            .flowOn(dispatcher)
    }

    override suspend fun getSignInMethodsForEmail(email: String): Either<Failure, List<SignInMethod>> =
        withContext(dispatcher) {
            authApi.getSignInMethodsForEmail(email)
        }


    //      ----------      SIGNIN/UP EMAIL        -------------------------------------------------

    override suspend fun signUpWithEmailAndPassword(
        profile: Profile, password: String
    ): Either<Failure, Profile> = withContext(dispatcher) {
        authApi
            .createUserWithEmailAndPassword(profile.email, password)
            .saveLastSignedInWith(SignInMethod.EMAIL)
            .suspendFlatMap { authResult ->
                authApi.updateProfile(name = profile.name, photoUrl = profile.photoUrl)
                    .map { authResult }
            }
            .suspendFlatMap {
                it.user.toProfile()
            }
    }

    override suspend fun signInWithEmailAndPassword(
        email: String, password: String
    ): Either<Failure, Profile> = withContext(dispatcher) {
        authApi
            .signInWithEmailAndPassword(email, password)
            .saveLastSignedInWith(SignInMethod.EMAIL)
            .suspendFlatMap {
                it.user.toProfile()
            }
    }

    override suspend fun sendPasswordResetEmail(email: String): Either<Failure, Unit> =
        withContext(dispatcher) {
            authApi.resetPassword(email)
        }


    //      ----------      SIGNIN/UP GOOGLE & FACEBOOK        -------------------------------------

    override suspend fun signInWithGoogleAccount(account: GoogleSignInAccount): Either<Failure, Profile> {
        val credential = account.getAuthCredential()
        return authApi
            .signInWithCredential(credential)
            .saveLastSignedInWith(SignInMethod.GOOGLE)
            .suspendFlatMap {
                it.user.toProfile()
            }
    }

    override suspend fun signInWithFacebookResult(result: LoginResult): Either<Failure, Profile> {
        val credential = result.getAuthCredential()
        return authApi
            .signInWithCredential(credential)
            .saveLastSignedInWith(SignInMethod.FACEBOOK)
            .suspendFlatMap { authResult ->
                val photoUrl = authResult.user?.photoUrl?.let {
                    Uri.parse("$it?access_token=${result.accessToken.token}")
                }
                authApi.updateProfile(photoUrl = photoUrl)
                    .map { authResult }
            }
            .suspendFlatMap {
                it.user.toProfile()
            }
    }


    //      ----------      UTIL FUNCTIONS TO SAVE PREFS     ----------------------------------------

    /*
     *  This method is necessary in order to correctly log out.
     */
    private suspend fun <L,R> Either<L,R>.saveLastSignedInWith(signInMethod: SignInMethod) : Either<L,R> {
        authPrefs.saveLastSignedInWith(signInMethod)
        return this
    }


}