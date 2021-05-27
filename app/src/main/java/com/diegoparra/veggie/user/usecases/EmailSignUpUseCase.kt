package com.diegoparra.veggie.user.usecases

import com.diegoparra.veggie.core.*
import com.diegoparra.veggie.user.entities_and_repo.SignInMethod
import com.diegoparra.veggie.user.entities_and_repo.UserConstants
import com.diegoparra.veggie.user.entities_and_repo.UserRepository
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
        //  TODO: SignUpFlow with Repository/Firebase
        return Either.Right(Unit)
    }


}