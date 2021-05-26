package com.diegoparra.veggie.user.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diegoparra.veggie.core.*
import com.diegoparra.veggie.user.usecases.EmailSignInUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class EmailSignInViewModel @Inject constructor(
    private val emailSignInUseCase: EmailSignInUseCase
) : ViewModel() {

    private val _email = MutableLiveData<Resource<String>>()
    val email: LiveData<Resource<String>> = _email

    private val _password = MutableLiveData<Resource<String>>()
    val password: LiveData<Resource<String>> = _password

    private val _navigateSignedIn = MutableLiveData<Event<Boolean>>()
    val navigateSignedIn: LiveData<Event<Boolean>> = _navigateSignedIn

    private val _toastFailure = MutableLiveData<Event<Failure>>()
    val toastFailure: LiveData<Event<Failure>> = _toastFailure


    fun setEmail(email: String) {
        _email.value = emailSignInUseCase.validateEmail(email).toResource()
    }

    fun setPassword(password: String) {
        _password.value = emailSignInUseCase.validatePassword(password).toResource()
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            emailSignInUseCase(EmailSignInUseCase.Params(email, password))
                .fold(::handleFailure) { handleSuccess(email, password) }
        }
    }

    private fun handleSuccess(email: String, password: String) {
        _email.value = Resource.Success(email)
        _password.value = Resource.Success(password)
        _navigateSignedIn.value = Event(true)
    }

    private fun handleFailure(failure: Failure) {
        when(failure){
            is SignInFailure.WrongInputList -> handleInputFailures(failure.failures)
            is SignInFailure.NewUser -> _toastFailure.value = Event(failure)
            is SignInFailure.SignInMethodNotLinked -> _toastFailure.value = Event(failure)
            else -> _toastFailure.value = Event(failure)
        }
    }

    private fun handleInputFailures(failures: List<SignInFailure.WrongInput>) {
        //  TODO: Reset failures (also in SignUpFragment)
        failures.forEach {
            when(it){
                is SignInFailure.WrongInput.Email -> _email.value = Resource.Error(it)
                is SignInFailure.WrongInput.Password -> _password.value = Resource.Error(it)
                else -> Timber.e("Failure: $it is not being managed")
            }
        }
    }

}