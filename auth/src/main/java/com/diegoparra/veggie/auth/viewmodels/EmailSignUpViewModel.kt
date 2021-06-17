package com.diegoparra.veggie.auth.viewmodels

import androidx.lifecycle.*
import com.diegoparra.veggie.auth.utils.AuthFailure
import com.diegoparra.veggie.auth.utils.Fields
import com.diegoparra.veggie.auth.usecases.auth.EmailSignUpUseCase
import com.diegoparra.veggie.core.kotlin.Event
import com.diegoparra.veggie.core.kotlin.Failure
import com.diegoparra.veggie.core.kotlin.Resource
import com.diegoparra.veggie.core.kotlin.toResource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class EmailSignUpViewModel @Inject constructor(
    private val emailSignUpUseCase: EmailSignUpUseCase,
    savedStateHandle: SavedStateHandle
): ViewModel() {

    private val _email = savedStateHandle.get<String>("email")!!    //  Come from navArgs

    private val _password = MutableStateFlow<Resource<String>>(Resource.Loading())
    val password: LiveData<Resource<String>> = _password.asLiveData()

    private val _name = MutableStateFlow<Resource<String>>(Resource.Loading())
    val name: LiveData<Resource<String>> = _name.asLiveData()

    val btnContinueEnabled: LiveData<Boolean> = combine(_password, _name) { it ->
        it.none { it !is Resource.Success }
    }.asLiveData()

    private val _loading = MutableLiveData<Boolean>(false)
    val loading: LiveData<Boolean> = _loading

    private val _navigateSuccess = MutableLiveData<Event<Boolean>>()
    val navigateSuccess: LiveData<Event<Boolean>> = _navigateSuccess

    private val _toastFailure = MutableLiveData<Event<Failure>>()
    val toastFailure: LiveData<Event<Failure>> = _toastFailure


    fun setName(name: String) {
        _name.value = emailSignUpUseCase.validateNameInput(name).toResource()
    }

    fun setPassword(password: String) {
        _password.value = emailSignUpUseCase.validatePasswordInput(password).toResource()
    }

    fun signUp(password: String, name: String) {
        Timber.d("signUp() called with: password = $password, name = $name")
        viewModelScope.launch {
            setLoadingState()
            val params = EmailSignUpUseCase.Params(
                email = _email,
                password = password,
                name = name
            )
            emailSignUpUseCase(params).fold(::handleFailure) { handleSuccess(params) }
        }
    }

    private fun setLoadingState() {
        Timber.d("setLoadingState() called")
        _loading.value = true
        _password.value = Resource.Loading()
        _name.value = Resource.Loading()
    }

    private fun handleSuccess(params: EmailSignUpUseCase.Params) {
        Timber.d("handleSuccess() called with: params = $params")
        _loading.value = false
        _password.value = Resource.Success(params.password)
        _name.value = Resource.Success(params.name)
        _navigateSuccess.value = Event(true)
    }

    private fun handleFailure(failure: Failure) {
        Timber.d("handleFailure() called with: failure = $failure")
        _loading.value = false
        when(failure) {
            is AuthFailure.ValidationFailures -> failure.failures.forEach { handleFailure(it) }
            is AuthFailure.WrongInput -> handleInputFailure(failure)
            else -> _toastFailure.value = Event(failure)
        }
    }

    private fun handleInputFailure(failure: AuthFailure.WrongInput) {
        when (failure.field) {
            Fields.PASSWORD -> _password.value = Resource.Error(failure)
            Fields.NAME -> _name.value = Resource.Error(failure)
            else -> _toastFailure.value = Event(failure)
        }
    }

}