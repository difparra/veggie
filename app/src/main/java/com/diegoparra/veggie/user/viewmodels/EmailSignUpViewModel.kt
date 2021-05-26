package com.diegoparra.veggie.user.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.diegoparra.veggie.core.*
import com.diegoparra.veggie.user.entities_and_repo.UserConstants
import com.diegoparra.veggie.user.usecases.EmailSignUpUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
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
        authenticate(EmailSignUpUseCase.Params(email, password, name))
    }

    override fun cleanErrorAdditionalFields(params: EmailSignUpUseCase.Params) {
        _name.value = Resource.Success(params.name)
    }

    override fun handleInputFailureOnAdditionalFields(inputFailures: Map<String, SignInFailure.WrongInput>) {
        _name.setIfNotNull(inputFailures[UserConstants.SignInFields.NAME]?.let { Resource.Error(it) })
    }

}