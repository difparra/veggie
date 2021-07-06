package com.diegoparra.veggie.auth.viewmodels

import androidx.lifecycle.*
import com.diegoparra.veggie.auth.utils.AuthFailure
import com.diegoparra.veggie.auth.usecases.auth.SendPasswordResetEmailUseCase
import com.diegoparra.veggie.auth.usecases.auth.EmailSignInUseCase
import com.diegoparra.veggie.core.kotlin.input_validation.InputFailure.Companion.Field
import com.diegoparra.veggie.core.kotlin.*
import com.diegoparra.veggie.core.kotlin.input_validation.InputFailure
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class EmailSignInViewModel @Inject constructor(
    private val emailSignInUseCase: EmailSignInUseCase,
    private val sendPasswordResetEmailUseCase: SendPasswordResetEmailUseCase,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    companion object {
        const val EMAIL_SAVED_STATE_KEY = "email"
    }

    private val _email = savedStateHandle.getLiveData<String>(EMAIL_SAVED_STATE_KEY)
    fun setSavedStateHandle(email: String) {
        savedStateHandle.set(EMAIL_SAVED_STATE_KEY, email)
    }

    private val _password = MutableStateFlow<Resource<String>>(Resource.Loading())
    val password: LiveData<Resource<String>> = _password.asLiveData()

    val btnContinueEnabled = _password.map { it is Resource.Success }.asLiveData()

    private val _loading = MutableLiveData<Boolean>(false)
    val loading: LiveData<Boolean> = _loading

    private val _navigateSuccess = MutableLiveData<Event<Boolean>>()
    val navigateSuccess: LiveData<Event<Boolean>> = _navigateSuccess

    private val _toastFailure = MutableLiveData<Event<Failure>>()
    val toastFailure: LiveData<Event<Failure>> = _toastFailure

    private val _passwordResetResult = MutableLiveData<Event<Resource<Unit>>>()
    val passwordResetResult: LiveData<Event<Resource<Unit>>> = _passwordResetResult


    fun setPassword(password: String) {
        Timber.d("setPassword called with password = $password")
        _password.value = emailSignInUseCase.validatePasswordInput(password).toResource()
    }

    fun signIn(password: String) {
        viewModelScope.launch {
            setLoadingState()
            val params = EmailSignInUseCase.Params(
                email = _email.value!!,
                password = password
            )
            emailSignInUseCase(params).fold(::handleFailure) { handleSuccess(params) }
        }
    }

    private fun setLoadingState() {
        _loading.value = true
        _password.value = Resource.Loading()
    }

    private fun handleSuccess(params: EmailSignInUseCase.Params) {
        _loading.value = false
        _password.value = Resource.Success(params.password)
        _navigateSuccess.value = Event(true)
    }

    private fun handleFailure(failure: Failure) {
        _loading.value = false
        when(failure) {
            is InputFailure.InputFailuresList -> failure.failures.forEach { handleInputFailure(it) }
            is InputFailure -> handleInputFailure(failure)
            else -> _toastFailure.value = Event(failure)
        }
    }

    private fun handleInputFailure(failure: InputFailure) {
        when (failure.field) {
            Field.PASSWORD -> _password.value = Resource.Error(failure)
            else -> _toastFailure.value = Event(failure)
        }
    }



    fun sendPasswordResetEmail(email: String) {
        viewModelScope.launch {
            val result = sendPasswordResetEmailUseCase(email)
            Timber.d("sendPasswordResetEmail: $result")
            _passwordResetResult.value = Event(result.toResource())
        }
    }

}