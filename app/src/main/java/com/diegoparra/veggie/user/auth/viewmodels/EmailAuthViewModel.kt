package com.diegoparra.veggie.user.auth.viewmodels

import androidx.lifecycle.*
import com.diegoparra.veggie.core.*
import com.diegoparra.veggie.user.auth.usecases.auth.EmailAuthUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

abstract class EmailAuthViewModel<Params : EmailAuthUseCase.EmailParams>(
    private val useCase: EmailAuthUseCase<Params>
) : ViewModel() {

    protected val _email = MutableStateFlow<Resource<String>>(Resource.Success(""))
    val email: LiveData<Resource<String>> = _email.asLiveData()

    protected val _password = MutableStateFlow<Resource<String>>(Resource.Success(""))
    val password: LiveData<Resource<String>> = _password.asLiveData()

    abstract val btnContinueEnabled: LiveData<Boolean>

    protected val _navigateSignedIn = MutableLiveData<Event<Boolean>>()
    val navigateSignedIn: LiveData<Event<Boolean>> = _navigateSignedIn

    protected val _toastFailure = MutableLiveData<Event<Failure>>()
    val toastFailure: LiveData<Event<Failure>> = _toastFailure


    /*
    //  It was working fine, but the validateNotEmailCollision has some quota limit in Firebase,
    //  and then it will incur in costs. So it is better to do just a simple format validation.
    private var emailJob: Job? = null
    fun setEmail(email: String) {
        when (val eitherValidField = useCase.validateEmail(email)) {
            is Either.Left -> _email.value = Resource.Error(eitherValidField.a)
            is Either.Right -> {
                emailJob?.cancel()
                emailJob = viewModelScope.launch {
                    _email.value = Resource.Loading()
                    when (val result = useCase.validateNotEmailCollision(email)) {
                        is Either.Left -> _email.value = Resource.Error(result.a)
                        is Either.Right -> _email.value = Resource.Success(email)
                    }
                }
            }
        }
    }*/

    fun setEmail(email: String) {
        Timber.d("setEmail called with email = $email")
        _email.value = useCase.validateEmail(email).toResource()
    }

    fun setPassword(password: String) {
        Timber.d("setPassword called with password = $password")
        _password.value = useCase.validatePassword(password).toResource()
    }


    fun authenticate(params: Params) {
        Timber.d("authenticate() called with: params = $params")
        viewModelScope.launch {
            cleanErrorStateAllFields(params)
            useCase(params).fold(
                ::handleFailure,
                ::handleSuccess
            )
        }
    }

    //  Set all fields to success, so that error is deleted
    protected fun cleanErrorStateAllFields(params: Params) {
        Timber.d("cleanErrorStateAllFields() called with: params = $params")
        _email.value = Resource.Success(params.email)
        _password.value = Resource.Success(params.password)
        cleanErrorAdditionalFields(params)
    }

    protected abstract fun cleanErrorAdditionalFields(params: Params)


    protected open fun handleSuccess(nothing: Unit) {
        Timber.d("handleSuccess() called")
        _navigateSignedIn.value = Event(true)
    }

    protected open fun handleFailure(failure: Failure) {
        Timber.d("handleFailure() called with: failure = $failure")
        when (failure) {
            is SignInFailure -> handleSignInFailure(failure)
            else -> _toastFailure.value = Event(failure)
        }
    }

    private fun handleSignInFailure(failure: SignInFailure) {
        when (failure) {
            is SignInFailure.ValidationFailures -> failure.failures.forEach { handleFailure(it) }
            is SignInFailure.WrongInput -> handleInputFailure(failure.field, failure)
            is SignInFailure.WrongSignInMethod -> _email.value = Resource.Error(failure)
            is SignInFailure.SignInState -> _toastFailure.value = Event(failure)
        }
    }

    private fun handleInputFailure(field: String, failure: SignInFailure.WrongInput) {
        Timber.d("handleInputFailure() called with: field = $field, failure = $failure")
        when (field) {
            Fields.EMAIL -> _email.value = Resource.Error(failure)
            Fields.PASSWORD -> _password.value = Resource.Error(failure)
            else -> handleInputFailureOnAdditionalFields(failure.field, failure)
        }
    }

    protected abstract fun handleInputFailureOnAdditionalFields(field: String, failure: Failure)

}