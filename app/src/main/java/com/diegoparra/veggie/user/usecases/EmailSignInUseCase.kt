package com.diegoparra.veggie.user.usecases

import com.diegoparra.veggie.core.*
import com.diegoparra.veggie.user.entities_and_repo.SignInMethod
import com.diegoparra.veggie.user.entities_and_repo.UserRepository
import javax.inject.Inject

class EmailSignInUseCase @Inject constructor(
    userRepository: UserRepository
) : EmailAuthUseCase<EmailSignInUseCase.Params>(userRepository) {

    data class Params(
        val email: String,
        val password: String
    )


    override fun validateFields(params: Params): List<Either<SignInFailure.WrongInput, String>> =
        listOf(
            validateEmail(params.email),
            validatePassword(params.password)
        )

    fun validateEmail(email: String): Either<SignInFailure.WrongInput, String> =
        TextInputValidation.forEmail(email)

    fun validatePassword(password: String): Either<SignInFailure.WrongInput, String> =
        if (password.isEmpty()) {
            Either.Left(SignInFailure.WrongInput.Password.Empty)
        } else {
            Either.Right(password)
        }

    override suspend fun validateEnabledAuthMethod(params: Params): Either<Failure, Unit> {
        return getSignInMethodsForEmail(params.email).flatMap {
            if (SignInMethod.EMAIL in it) {
                Either.Right(Unit)
            } else if (it.isEmpty()) {
                Either.Left(SignInFailure.NewUser)
            } else {
                Either.Left(
                    SignInFailure.SignInMethodNotLinked(
                        signInMethod = SignInMethod.EMAIL.toString(),
                        linkedSignInMethods = it.map { it.toString() })
                )
            }
        }
    }

    override suspend fun signInRepository(params: Params): Either<Failure, Unit> {
        //  TODO: SignInFlow with Repository/Firebase
        return Either.Right(Unit)
    }

}