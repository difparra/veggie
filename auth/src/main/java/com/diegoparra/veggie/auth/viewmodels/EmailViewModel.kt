package com.diegoparra.veggie.auth.viewmodels

import androidx.lifecycle.*
import com.diegoparra.veggie.auth.usecases.auth.ValidateEmailAuthUseCase
import com.diegoparra.veggie.auth.utils.AuthFailure
import com.diegoparra.veggie.core.kotlin.Event
import com.diegoparra.veggie.core.kotlin.Resource
import com.diegoparra.veggie.core.kotlin.toResource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EmailViewModel @Inject constructor(
    private val validateEmailAuthUseCase: ValidateEmailAuthUseCase
) : ViewModel() {

    private val _email = MutableStateFlow<Resource<String>>(Resource.Loading())
    val email: LiveData<Resource<String>> = _email.asLiveData()

    private val _loading = MutableLiveData<Boolean>(false)
    val loading: LiveData<Boolean> = _loading

    private val _toastFailure = MutableLiveData<Event<AuthFailure>>()
    val toastFailure: LiveData<Event<AuthFailure>> = _toastFailure

    val btnContinueEnabled: LiveData<Boolean> =
        _email.map { it is Resource.Success }.asLiveData()

    private val _navigateSuccess =
        MutableLiveData<Event<ValidateEmailAuthUseCase.EmailAuthMethod>>()
    val navigateEmailAuthMethod: LiveData<Event<ValidateEmailAuthUseCase.EmailAuthMethod>> =
        _navigateSuccess



    fun setEmail(email: String) {
        _email.value = validateEmailAuthUseCase.validateEmailInput(email).toResource()
    }

    fun onContinueClicked(email: String) {
        viewModelScope.launch {
            _loading.value = true
            validateEmailAuthUseCase(email).fold(::handleFailure, ::handleSuccess)
        }
    }


    private fun handleSuccess(emailAuthMethod: ValidateEmailAuthUseCase.EmailAuthMethod) {
        _loading.value = false
        _navigateSuccess.value = Event(emailAuthMethod)
    }

    private fun handleFailure(failure: AuthFailure) {
        _loading.value = false
        when(failure) {
            is AuthFailure.WrongInput -> _email.value = Resource.Error(failure)
            else -> _toastFailure.value = Event(failure)
        }
    }

}