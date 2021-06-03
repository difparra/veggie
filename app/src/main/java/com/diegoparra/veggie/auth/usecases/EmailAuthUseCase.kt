package com.diegoparra.veggie.auth.usecases

import com.diegoparra.veggie.core.Either
import com.diegoparra.veggie.core.Failure
import com.diegoparra.veggie.core.SignInFailure
import com.diegoparra.veggie.auth.domain.AuthConstants
import com.diegoparra.veggie.auth.domain.AuthRepository
import com.diegoparra.veggie.auth.usecases.utils.EmailCollisionValidation
import com.diegoparra.veggie.auth.usecases.utils.TextInputValidation
import timber.log.Timber

abstract class EmailAuthUseCase<Params : EmailAuthUseCase.EmailParams>(
    protected val authRepository: AuthRepository
) {
    protected val emailCollisionValidation = EmailCollisionValidation(authRepository)


    interface EmailParams {
        val email: String
        val password: String
    }

    suspend operator fun invoke(params: Params): Either<Failure, Unit> {
        initialValidation(params).let {
            Timber.d("initialValidation(params) called. Result: $it")
            if (it is Either.Left) {
                return it
            }
        }
        signInRepository(params).let {
            Timber.d("signInRepository(params) called. Result: $it")
            if (it is Either.Left) {
                return it
            }
        }
        Timber.d("invoke() called with result: Right(Unit)")
        return Either.Right(Unit)
    }


    //      ----------------------------------------------------------------------------------------

    private suspend fun initialValidation(params: Params): Either<SignInFailure.ValidationFailures, Unit> {

        //  Get input failures
        val inputs =
            validateAdditionalFields(params) + validateEmail(params.email) + validatePassword(params.password)
        val inputFailures =
            inputs.filter { it is Either.Left }.map { (it as Either.Left).a }.toSet()

        //  If email is correct, get if failure account linking or null
        var linkedEmailFailure: Failure? = null
        val isEmailCorrect = inputFailures.none { it.field == AuthConstants.Fields.EMAIL }
        if (isEmailCorrect) {
            validateNotEmailCollision(params.email).let {
                if (it is Either.Left) {
                    linkedEmailFailure = it.a
                }
            }
        }

        //  Get failures from input + linkedAccount
        val totalFailures = linkedEmailFailure?.let {
            inputFailures + it
        } ?: inputFailures

        //  Add failures and send validation result
        return if (totalFailures.isNullOrEmpty()) {
            Either.Right(Unit)
        } else {
            Either.Left(SignInFailure.ValidationFailures(totalFailures))
        }

    }


    //      ----------------------------------------------------------------------------------------

    open fun validateEmail(email: String): Either<SignInFailure.WrongInput, String> =
        TextInputValidation.forEmail(email)

    open fun validatePassword(password: String): Either<SignInFailure.WrongInput, String> =
        TextInputValidation.forPassword(password)

    abstract fun validateAdditionalFields(params: Params): Set<Either<SignInFailure.WrongInput, String>>


    //      ----------------------------------------------------------------------------------------

    abstract suspend fun validateNotEmailCollision(email: String): Either<Failure, Unit>


    //      ----------------------------------------------------------------------------------------

    protected abstract suspend fun signInRepository(params: Params): Either<Failure, Unit>

}