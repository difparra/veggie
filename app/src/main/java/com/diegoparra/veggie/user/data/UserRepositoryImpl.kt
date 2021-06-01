package com.diegoparra.veggie.user.data

import com.diegoparra.veggie.core.*
import com.diegoparra.veggie.products.IoDispatcher
import com.diegoparra.veggie.user.data.ToFirebaseTransformations.getAuthCredential
import com.diegoparra.veggie.user.data.ToFirebaseTransformations.getProfileInfoFirebase
import com.diegoparra.veggie.user.data.UserTransformations.toBasicUserInfo
import com.diegoparra.veggie.user.data.UserTransformations.toIsSignedIn
import com.diegoparra.veggie.user.data.UserTransformations.toSignInMethodList
import com.diegoparra.veggie.user.entities_and_repo.BasicUserInfo
import com.diegoparra.veggie.user.entities_and_repo.SignInMethod
import com.diegoparra.veggie.user.entities_and_repo.User
import com.diegoparra.veggie.user.entities_and_repo.UserRepository
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userApi: UserApi,
    private val userPrefs: UserPrefs,
    @IoDispatcher private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : UserRepository {


    //      ----------      BASIC OPERATIONS        ------------------------------------------------

    override fun isSignedIn(): Flow<Boolean> {
        return userApi
            .getCurrentUserAsFlow()
            .map { it.toIsSignedIn() }
            .flowOn(dispatcher)
    }

    override fun signOut() {
        userApi.signOut()
    }


    //      ----------      BASIC INFORMATION        -----------------------------------------------

    override fun getBasicUserInfo(): Flow<Either<Failure, BasicUserInfo>> {
        return userApi
            .getCurrentUserAsFlow()
            .map { it.toBasicUserInfo() }
            .flowOn(dispatcher)
    }

    override suspend fun getSignInMethodsForEmail(email: String): Either<Failure, List<SignInMethod>> =
        withContext(dispatcher) {
            userApi
                .getSignInMethodsForEmail(email)
                .map { it.toSignInMethodList() }
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
    }

    override suspend fun signInWithEmailAndPassword(
        email: String, password: String
    ): Either<Failure, Unit> = withContext(dispatcher) {
        userApi
            .signInWithEmailAndPassword(email, password)
    }

    override suspend fun sendPasswordResetEmail(email: String): Either<Failure, Unit> =
        withContext(dispatcher) {
            userApi.resetPassword(email)
        }


    //      ----------      SIGNIN/UP GOOGLE        ------------------------------------------------

    override suspend fun signInWithGoogleAccount(account: GoogleSignInAccount): Either<Failure, Unit> {
        val credential = account.getAuthCredential()
        val profileInfo = account.getProfileInfoFirebase()
        return userApi
            .signInWithCredential(credential)
            .suspendFlatMap {
                userApi.updateProfile(profileInfo)
            }
    }

}