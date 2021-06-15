package com.diegoparra.veggie.auth.usecases.auth

import com.diegoparra.veggie.core.kotlin.Either
import com.diegoparra.veggie.core.kotlin.Failure
import com.diegoparra.veggie.auth.domain.AuthRepository
import com.diegoparra.veggie.auth.usecases.utils.EmailCollisionValidation
import com.diegoparra.veggie.auth.utils.AuthCallbacks
import com.diegoparra.veggie.auth.utils.AuthFailure
import com.diegoparra.veggie.auth.utils.Fields.EMAIL
import com.diegoparra.veggie.auth.utils.TextInputValidation

abstract class EmailAuthUseCase<Params : EmailAuthUseCase.EmailParams>(
    authRepository: AuthRepository,
    authCallbacks: AuthCallbacks
) : AuthUseCase<Params>(authRepository, authCallbacks) {

    protected val emailCollisionValidation = EmailCollisionValidation(authRepository)

    interface EmailParams {
        val email: String
        val password: String
    }


    //      ----------------------------------------------------------------------------------------

    override suspend fun validate(params: Params): Either<AuthFailure.ValidationFailures, Unit> {

        //  Get input failures
        val inputs =
            validateAdditionalFields(params) + validateEmail(params.email) + validatePassword(params.password)
        val inputFailures =
            inputs.filter { it is Either.Left }.map { (it as Either.Left).a }.toSet()

        //  If email is correct, get if failure account linking or null
        var linkedEmailFailure: Failure? = null
        val isEmailCorrect = inputFailures.none { it.field == EMAIL }
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
            Either.Left(AuthFailure.ValidationFailures(totalFailures))
        }
    }

    //      ----------------------------------------------------------------------------------------

    open fun validateEmail(email: String): Either<AuthFailure.WrongInput, String> =
        TextInputValidation.forEmail(email)

    open fun validatePassword(password: String): Either<AuthFailure.WrongInput, String> =
        TextInputValidation.forPassword(password)

    abstract fun validateAdditionalFields(params: Params): Set<Either<AuthFailure.WrongInput, String>>


    //      ----------------------------------------------------------------------------------------

    abstract suspend fun validateNotEmailCollision(email: String): Either<Failure, Unit>


}