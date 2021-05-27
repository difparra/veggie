package com.diegoparra.veggie.user.viewmodels

import androidx.lifecycle.*
import com.diegoparra.veggie.core.*
import com.diegoparra.veggie.user.usecases.EmailSignInUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

@HiltViewModel
class EmailSignInViewModel @Inject constructor(
    private val emailSignInUseCase: EmailSignInUseCase
) : EmailAuthViewModel<EmailSignInUseCase.Params>(emailSignInUseCase) {

    override val btnContinueEnabled: LiveData<Boolean>
        get() = combine(_email, _password) { it ->
            it.any { it !is Resource.Success }
        }.asLiveData()


    fun signIn(email: String, password: String) {
        authenticate(EmailSignInUseCase.Params(email, password))
    }

    override fun cleanErrorAdditionalFields(params: EmailSignInUseCase.Params) {
        //  No additional fields to email and password. There is no need to clean.
    }

    override fun handleInputFailureOnAdditionalFields(field: String, failure: Failure) {
        //  No additional fields to email and password. There is no need to clean.
    }

}