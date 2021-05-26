package com.diegoparra.veggie.user.usecases

import com.diegoparra.veggie.core.Either
import com.diegoparra.veggie.core.Failure
import com.diegoparra.veggie.core.SignInFailure
import com.diegoparra.veggie.user.entities_and_repo.SignInMethod
import com.diegoparra.veggie.user.entities_and_repo.UserRepository
import timber.log.Timber

abstract class EmailAuthUseCase<Params : EmailAuthUseCase.EmailParams>(
    protected val userRepository: UserRepository
) {

    interface EmailParams {
        val email: String
        val password: String
    }

    suspend operator fun invoke(params: Params): Either<Failure, Unit> {
        validateFields(params).let {
            Timber.d("fields validation: $it")
            val failures =
                it.filter { it.value is Either.Left }.mapValues { (it.value as Either.Left).a }
            Timber.d("failures: $it")
            if (failures.isNotEmpty()) {
                Timber.d("Result sent: ${Either.Left(SignInFailure.WrongInputs(failures))}")
                return Either.Left(SignInFailure.WrongInputs(failures))
            }
        }
        validateEmailLinkedWithAuthMethod(params.email).let {
            if (it is Either.Left) {
                return it
            }
        }
        signInRepository(params).let {
            if (it is Either.Left) {
                return it
            }
        }
        return Either.Right(Unit)
    }

    /*
        TODO:   move emaillinkedWithAuthMethod validation into validateFields.
                This is because if pressing continue
                Move failureNotLinkedEmail to WrongInputFailure
     */

    abstract fun validateFields(params: Params): Map<String, Either<SignInFailure.WrongInput, String>>

    open fun validateEmail(email: String): Either<SignInFailure.WrongInput, String> =
        TextInputValidation.forEmail(email)

    open fun validatePassword(password: String): Either<SignInFailure.WrongInput, String> =
        TextInputValidation.forPassword(password)



    abstract suspend fun validateEmailLinkedWithAuthMethod(email: String): Either<Failure, Unit>

    protected suspend fun getSignInMethodsForEmail(email: String): Either<Failure, List<SignInMethod>> {
        return userRepository.getSignInMethodsForEmail(email)
    }


    protected abstract suspend fun signInRepository(params: Params): Either<Failure, Unit>

}