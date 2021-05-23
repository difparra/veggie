package com.diegoparra.veggie.user.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.diegoparra.veggie.core.Either
import com.diegoparra.veggie.core.Failure
import com.diegoparra.veggie.core.Resource
import com.diegoparra.veggie.core.validateEmail
import com.diegoparra.veggie.user.usecases.IsEmailRegisteredUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class EmailAuthViewModel @Inject constructor(
    private val isEmailRegisteredUseCase: IsEmailRegisteredUseCase
) : ViewModel() {

    /*
        TODO:
            emailState problem with LiveData and navigation -> can't get back from signIn to email fragment
            check rotating (app crashed before but it is now not crashing)
            hilt nav graph view model
            maybe create new layout for signInOptions horizontal, move app icon to the left, and
                check scroll views (e.g. in signIn horizontal screen a scroll view must be added)
            Correct navigation to signIn-signUp -> Should navigate to signIn and in there
                validate data, if correct stay there but if not move to signUp.
     */

    private val _email = MutableStateFlow<String?>(null)
    val email = _email.filterNotNull().asLiveData()
    fun setEmail(email: String) : Either<Failure, Nothing?> {
        Timber.d(email)
        return if(email.isEmpty()){
            _email.value = null
            Either.Left(Failure.SignInFailure.EmptyField)
        }else if(!validateEmail(email)){
            _email.value = null
            Either.Left(Failure.SignInFailure.InvalidEmail)
        }else{
            _email.value = email
            Either.Right(null)
        }
    }

    val isEmailRegistered = _email.map {
        if(it.isNullOrEmpty()){
            Resource.Error(Failure.SignInFailure.EmptyField)
        }else{
            when(val result = isEmailRegisteredUseCase(it)){
                is Either.Right -> Resource.Success(result.b)
                is Either.Left -> Resource.Error(result.a)
            }
        }
    }.asLiveData()

}