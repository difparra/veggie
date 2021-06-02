package com.diegoparra.veggie.user.data

import com.diegoparra.veggie.core.*
import com.diegoparra.veggie.products.IoDispatcher
import com.diegoparra.veggie.user.data.utils.ToFirebaseTransformations.getAuthCredential
import com.diegoparra.veggie.user.data.utils.ToFirebaseTransformations.getProfileInfoFirebase
import com.diegoparra.veggie.user.data.UserTransformations.toBasicUserInfo
import com.diegoparra.veggie.user.data.UserTransformations.isSignedIn
import com.diegoparra.veggie.user.data.firebase.ProfileInfoFirebase
import com.diegoparra.veggie.user.data.firebase.UserApi
import com.diegoparra.veggie.user.data.prefs.UserPrefs
import com.diegoparra.veggie.user.entities_and_repo.BasicUserInfo
import com.diegoparra.veggie.user.entities_and_repo.SignInMethod
import com.diegoparra.veggie.user.entities_and_repo.User
import com.diegoparra.veggie.user.entities_and_repo.UserRepository
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

class UserRepositoryImpl @Inject constructor(
    private val userApi: UserApi,
    private val userPrefs: UserPrefs,
    private val googleSignInClient: GoogleSignInClient,
    private val loginManager: LoginManager,
    @IoDispatcher private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : UserRepository {


    //      ----------      BASIC OPERATIONS        ------------------------------------------------

    override fun isSignedIn(): Flow<Boolean> {
        return userApi
            .getCurrentUserAsFlow()
            .map { it.isSignedIn() }
            .flowOn(dispatcher)
    }

    override suspend fun signOut() {
        Timber.d("sign out")
        userApi.signOut()
        if (userPrefs.getLastSignedInWith() == SignInMethod.FACEBOOK) {
            Timber.d("sign out with facebook")
            loginManager.logOut()
        }
        if (userPrefs.getLastSignedInWith() == SignInMethod.GOOGLE) {
            Timber.d("sign out with google")
            googleSignInClient.signOut()
        }
    }


    //      ----------      BASIC INFORMATION        -----------------------------------------------

    override fun getBasicUserInfo(): Flow<Either<Failure, BasicUserInfo>> {
        return userApi
            .getCurrentUserAsFlow()
            .map {
                it.toBasicUserInfo(
                    userPrefs.getLastSignedInWith(),
                    userPrefs.getFacebookAccessToken()
                )
            }
            .flowOn(dispatcher)
    }

    override suspend fun getSignInMethodsForEmail(email: String): Either<Failure, List<SignInMethod>> =
        withContext(dispatcher) {
            userApi.getSignInMethodsForEmail(email)
        }


    //      ----------      SIGNIN/UP EMAIL        -------------------------------------------------

    override suspend fun signUpWithEmailAndPassword(
        user: User, password: String
    ): Either<Failure, Unit> = withContext(dispatcher) {
        userApi
            .createUserWithEmailAndPassword(user.email, password)
            .suspendFlatMap {
                userApi.updateProfile(ProfileInfoFirebase(user.name, user.photoUrl))
            }
            .savePrefsSignedInWith(SignInMethod.EMAIL)
    }

    override suspend fun signInWithEmailAndPassword(
        email: String, password: String
    ): Either<Failure, Unit> = withContext(dispatcher) {
        userApi
            .signInWithEmailAndPassword(email, password)
            .savePrefsSignedInWith(SignInMethod.EMAIL)
    }

    override suspend fun sendPasswordResetEmail(email: String): Either<Failure, Unit> =
        withContext(dispatcher) {
            userApi.resetPassword(email)
        }


    //      ----------      SIGNIN/UP GOOGLE & FACEBOOK        -------------------------------------

    override suspend fun signInWithGoogleAccount(account: GoogleSignInAccount): Either<Failure, Unit> {
        val credential = account.getAuthCredential()
        return userApi
            .signInWithCredential(credential)
            .savePrefsSignedInWith(SignInMethod.GOOGLE)
    }

    override suspend fun signInWithFacebookResult(result: LoginResult): Either<Failure, Unit> {
        val credential = result.getAuthCredential()
        return userApi
            .signInWithCredential(credential)
            .saveFbAccessToken(result.accessToken.token)
            .savePrefsSignedInWith(SignInMethod.FACEBOOK)
    }


    //      ----------      UTIL FUNCTIONS TO SAVE PREFS     ----------------------------------------

    private suspend fun <L, R> Either<L, R>.savePrefsSignedInWith(signInMethod: SignInMethod): Either<L, R> {
        if (this is Either.Right) {
            userPrefs.saveLastSignedInWith(signInMethod)
        }
        return this
    }

    private suspend fun <L, R> Either<L, R>.saveFbAccessToken(token: String): Either<L, R> {
        if (this is Either.Right) {
            userPrefs.saveFacebookAccessToken(token)
        }
        return this
    }

}