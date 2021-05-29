package com.diegoparra.veggie.user.usecases

import com.diegoparra.veggie.core.*
import com.diegoparra.veggie.user.entities_and_repo.SignInMethod
import com.diegoparra.veggie.user.entities_and_repo.User
import com.diegoparra.veggie.user.entities_and_repo.UserRepository
import timber.log.Timber
import javax.inject.Inject

class EmailSignUpUseCase @Inject constructor(
    userRepository: UserRepository
) : EmailAuthUseCase<EmailSignUpUseCase.Params>(userRepository) {

    data class Params(
        override val email: String,
        override val password: String,
        val name: String,
    ) : EmailParams


    fun validateName(name: String): Either<SignInFailure.WrongInput, String> =
        TextInputValidation.forName(name)

    override fun validateAdditionalFields(params: Params): Set<Either<SignInFailure.WrongInput, String>> =
        setOf(validateName(params.name))


    override suspend fun validateEmailLinkedWithAuthMethod(email: String): Either<Failure, Unit> {
        return getSignInMethodsForEmail(email).flatMap {
            Timber.d("email: $email - signInMethodsList: ${it.joinToString()}")
            if (it.isEmpty()) {
                Either.Right(Unit)
            } else if (SignInMethod.EMAIL in it) {
                Either.Left(SignInFailure.WrongSignInMethod.ExistentUser)
            } else {
                Either.Left(
                    SignInFailure.WrongSignInMethod.SignInMethodNotLinked(
                        signInMethod = SignInMethod.EMAIL.toString(),
                        linkedSignInMethods = it.map { it.toString() })
                )
            }
        }
    }

    override suspend fun signInRepository(params: Params): Either<Failure, Unit> {
        val user = User(
            id = "",        //  Will be actually created by the repository
            email = params.email,
            name = params.name,
            phoneNumber = null,
            address = null,
            photoUrl = null
        )
        return userRepository.signUpWithEmailAndPassword(user, params.password)
    }


}