package com.diegoparra.veggie.user.usecases

import com.diegoparra.veggie.core.TextInputValidation
import com.diegoparra.veggie.core.Either
import com.diegoparra.veggie.core.Failure
import com.diegoparra.veggie.core.SignInFailure
import javax.inject.Inject


class SaveProfileUseCase @Inject constructor() {

    fun validateName(name: String): Either<SignInFailure.WrongInput, String> {
        return TextInputValidation.forName(name)
    }

    fun saveName(name: String) : Either<Failure, Unit> {
        validateName(name).let {
            if(it is Either.Left) {
                return it
            }
        }
        //  TODO
        return Either.Right(Unit)
    }


}