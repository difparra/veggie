package com.diegoparra.veggie.user.data

import com.diegoparra.veggie.core.*
import com.diegoparra.veggie.products.IoDispatcher
import com.diegoparra.veggie.user.data.UserTransformations.toSignInMethodList
import com.diegoparra.veggie.user.entities_and_repo.BasicUserInfo
import com.diegoparra.veggie.user.entities_and_repo.SignInMethod
import com.diegoparra.veggie.user.entities_and_repo.User
import com.diegoparra.veggie.user.entities_and_repo.UserRepository
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


    override fun isSignedIn(): Flow<Boolean> {
        return userApi
            .getCurrentUserAsFlow()
            .map {
                it != null && !it.isAnonymous
            }
            .flowOn(dispatcher)
    }

    override fun getBasicUserInfo(): Flow<Either<Failure, BasicUserInfo>> {
        return userApi
            .getCurrentUserAsFlow()
            .map {
                if(it==null) {
                    Either.Left(SignInFailure.SignInState.NotSignedIn)
                }else if(it.isAnonymous){
                    Either.Left(SignInFailure.SignInState.Anonymous)
                }else{
                    val user = BasicUserInfo(
                        id = it.uid,
                        email = it.email!!,
                        name = it.displayName ?: it.email!!.substringBefore('@')
                    )
                    Either.Right(user)
                }
            }
    }

    override suspend fun getSignInMethodsForEmail(email: String): Either<Failure, List<SignInMethod>> =
        withContext(dispatcher) {
            userApi.getSignInMethodsForEmail(email)
                .map { it.toSignInMethodList() }
        }

    override suspend fun signUpWithEmailAndPassword(
        user: User, password: String
    ): Either<Failure, Unit> = withContext(dispatcher) {
        userApi
            .createUserWithEmailAndPassword(user.email, password)
            .suspendFlatMap {
                userApi.updateProfile(user.name, user.photoUrl)
            }
    }

    override suspend fun signInWithEmailAndPassword(
        email: String, password: String
    ): Either<Failure, Unit> = withContext(dispatcher) {
        userApi
            .signInWithEmailAndPassword(email, password)
    }

    override fun signOut() {
        userApi.signOut()
    }

}