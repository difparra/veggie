package com.diegoparra.veggie.user.usecases

import com.diegoparra.veggie.core.*
import com.diegoparra.veggie.user.entities_and_repo.SignInMethod
import com.diegoparra.veggie.user.entities_and_repo.UserConstants
import com.diegoparra.veggie.user.entities_and_repo.UserRepository
import javax.inject.Inject

class EmailSignInUseCase @Inject constructor(
    userRepository: UserRepository
) : EmailAuthUseCase<EmailSignInUseCase.Params>(userRepository) {

    data class Params(
        override val email: String,
        override val password: String
    ) : EmailParams


    override fun validateFields(params: Params): Map<String, Either<SignInFailure.WrongInput, String>> =
        mapOf(
            UserConstants.SignInFields.EMAIL to validateEmail(params.email),
            UserConstants.SignInFields.PASSWORD to validatePassword(params.password)
        )

    override fun validatePassword(password: String): Either<SignInFailure.WrongInput, String> {
        val validation = TextInputValidation.forPassword(password)
        return if(validation is Either.Left && validation.a is SignInFailure.WrongInput.Short){
            Either.Right(password)
        }else{
            validation
        }
    }


    override suspend fun validateEmailLinkedWithAuthMethod(email: String): Either<Failure, Unit> {
        return getSignInMethodsForEmail(email).flatMap {
            if (SignInMethod.EMAIL in it) {
                Either.Right(Unit)
            } else if (it.isEmpty()) {
                Either.Left(SignInFailure.WrongSignInMethod.NewUser)
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
        //  TODO: SignInFlow with Repository/Firebase
        return Either.Right(Unit)
    }

}