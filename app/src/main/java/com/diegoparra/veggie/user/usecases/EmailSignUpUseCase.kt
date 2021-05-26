package com.diegoparra.veggie.user.usecases

import com.diegoparra.veggie.core.*
import com.diegoparra.veggie.user.entities_and_repo.SignInMethod
import com.diegoparra.veggie.user.entities_and_repo.UserRepository
import javax.inject.Inject

class EmailSignUpUseCase @Inject constructor(
    userRepository: UserRepository
) : EmailAuthUseCase<EmailSignUpUseCase.Params>(userRepository) {

    data class Params(
        val email: String,
        val name: String,
        val password: String
    )

    override fun validateFields(params: Params): List<Either<SignInFailure.WrongInput, String>> =
        listOf(
            validateEmail(params.email),
            validateName(params.name),
            validatePassword(params.password)
        )

    fun validateEmail(email: String): Either<SignInFailure.WrongInput, String> =
        TextInputValidation.forEmail(email)

    fun validateName(name: String): Either<SignInFailure.WrongInput, String> =
        TextInputValidation.forName(name)

    fun validatePassword(password: String): Either<SignInFailure.WrongInput, String> =
        TextInputValidation.forPassword(password)

    override suspend fun validateEnabledAuthMethod(params: Params): Either<Failure, Unit> {
        return getSignInMethodsForEmail(params.email).flatMap {
            if(it.isEmpty()){
                Either.Right(Unit)
            }else if(SignInMethod.EMAIL in it){
                Either.Left(SignInFailure.ExistentUser)
            }else{
                Either.Left(
                    SignInFailure.SignInMethodNotLinked(
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