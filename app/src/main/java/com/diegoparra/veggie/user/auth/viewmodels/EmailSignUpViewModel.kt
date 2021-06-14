package com.diegoparra.veggie.user.auth.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.diegoparra.veggie.core.*
import com.diegoparra.veggie.user.auth.usecases.auth.EmailSignUpUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class EmailSignUpViewModel @Inject constructor(
    private val emailSignUpUseCase: EmailSignUpUseCase
) : EmailAuthViewModel<EmailSignUpUseCase.Params>(emailSignUpUseCase) {

    private val _name = MutableStateFlow<Resource<String>>(Resource.Success(""))
    val name: LiveData<Resource<String>> = _name.asLiveData()

    override val btnContinueEnabled: LiveData<Boolean>
        get() = combine(_email, _password, _name) { it ->
            it.any { it !is Resource.Success }
        }.asLiveData()

    fun setName(name: String) {
        _name.value = emailSignUpUseCase.validateName(name).toResource()
    }

    fun signUp(email: String, password: String, name: String) {
        Timber.d("signUp() called with: email = $email, password = $password, name = $name")
        authenticate(EmailSignUpUseCase.Params(email, password, name))
    }

    override fun cleanErrorAdditionalFields(params: EmailSignUpUseCase.Params) {
        Timber.d("cleanErrorAdditionalFields() called with: params = $params")
        _name.value = Resource.Success(params.name)
    }

    override fun handleInputFailureOnAdditionalFields(field: String, failure: Failure) {
        Timber.d("handleInputFailureOnAdditionalFields() called with: field = $field, failure = $failure")
        if (field == Fields.NAME) {
            _name.value = Resource.Error(failure)
        }
    }

}