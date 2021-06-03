package com.diegoparra.veggie.auth.viewmodels

import androidx.lifecycle.*
import com.diegoparra.veggie.core.*
import com.diegoparra.veggie.auth.usecases.SendPasswordResetEmailUseCase
import com.diegoparra.veggie.auth.usecases.EmailSignInUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class EmailSignInViewModel @Inject constructor(
    private val emailSignInUseCase: EmailSignInUseCase,
    private val sendPasswordResetEmailUseCase: SendPasswordResetEmailUseCase
) : EmailAuthViewModel<EmailSignInUseCase.Params>(emailSignInUseCase) {

    override val btnContinueEnabled: LiveData<Boolean>
        get() = combine(_email, _password) { it ->
            it.any { it !is Resource.Success }
        }.asLiveData()


    private val _passwordResetResult = MutableLiveData<Event<Resource<Unit>>>()
    val passwordResetResult: LiveData<Event<Resource<Unit>>> = _passwordResetResult


    fun signIn(email: String, password: String) {
        authenticate(EmailSignInUseCase.Params(email, password))
    }

    override fun cleanErrorAdditionalFields(params: EmailSignInUseCase.Params) {
        //  No additional fields to email and password. There is no need to clean.
    }

    override fun handleInputFailureOnAdditionalFields(field: String, failure: Failure) {
        //  No additional fields to email and password. There is no need to clean.
    }

    fun sendPasswordResetEmail(email: String) {
        viewModelScope.launch {
            val result = sendPasswordResetEmailUseCase(email)
            Timber.d("sendPasswordResetEmail: $result")
            _passwordResetResult.value = Event(result.toResource())
        }
    }

}