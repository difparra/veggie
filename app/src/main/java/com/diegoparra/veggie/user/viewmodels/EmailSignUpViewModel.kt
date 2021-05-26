package com.diegoparra.veggie.user.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diegoparra.veggie.core.*
import com.diegoparra.veggie.user.usecases.EmailSignInUseCase
import com.diegoparra.veggie.user.usecases.EmailSignUpUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EmailSignUpViewModel @Inject constructor(
    private val emailSignUpUseCase: EmailSignUpUseCase
) : ViewModel() {

    private val _email = MutableLiveData<Resource<String>>()
    val email: LiveData<Resource<String>> = _email

    private val _password = MutableLiveData<Resource<String>>()
    val password: LiveData<Resource<String>> = _password

    private val _name = MutableLiveData<Resource<String>>()
    val name: LiveData<Resource<String>> = _name

    private val _navigateSignedIn = MutableLiveData<Event<Boolean>>()
    val navigateSignedIn: LiveData<Event<Boolean>> = _navigateSignedIn

    private val _toastFailure = MutableLiveData<Event<Failure>>()
    val toastFailure: LiveData<Event<Failure>> = _toastFailure





    fun setEmail(email: String) {
        _email.value = emailSignUpUseCase.validateEmail(email).toResource()
    }

    fun setPassword(password: String) {
        _password.value = emailSignUpUseCase.validatePassword(password).toResource()
    }

    fun setName(name: String){
        _name.value = emailSignUpUseCase.validateName(name).toResource()
    }


    fun signUp(email: String, name: String, password: String) {
        viewModelScope.launch {
            emailSignUpUseCase(EmailSignUpUseCase.Params(email, name, password))
                .fold(::handleFailure) { handleSuccess(email, name, password) }
        }
    }

    private fun handleSuccess(email: String, name: String, password: String) {
        _email.value = Resource.Success(email)
        _name.value = Resource.Success(name)
        _password.value = Resource.Success(password)
        _navigateSignedIn.value = Event(true)
    }

    private fun handleFailure(failure: Failure) {
        when(failure){
            is SignInFailure.WrongInputList -> handleInputFailures(failure.failures)
            is SignInFailure.ExistentUser -> _toastFailure.value = Event(failure)
            is SignInFailure.SignInMethodNotLinked -> _toastFailure.value = Event(failure)
            else -> _toastFailure.value = Event(failure)
        }
    }

    private fun handleInputFailures(failures: List<SignInFailure.WrongInput>) {
        failures.forEach {
            when(it){
                is SignInFailure.WrongInput.Email -> _email.value = Resource.Error(it)
                is SignInFailure.WrongInput.NameEmpty -> _name.value = Resource.Error(it)
                is SignInFailure.WrongInput.Password -> _password.value = Resource.Error(it)
            }
        }
    }


}