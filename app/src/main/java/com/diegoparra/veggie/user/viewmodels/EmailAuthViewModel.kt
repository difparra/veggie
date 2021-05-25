package com.diegoparra.veggie.user.viewmodels

import androidx.lifecycle.*
import com.diegoparra.veggie.core.*
import com.diegoparra.veggie.user.entities_and_repo.IsNewUser
import com.diegoparra.veggie.user.usecases.IsEmailValidForSignIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EmailAuthViewModel @Inject constructor(
    private val isEmailValidForSignIn: IsEmailValidForSignIn
) : ViewModel() {

    private val _email = MutableLiveData<Resource<String>>()
    val email: LiveData<Resource<String>> = _email

    private val _navigate = MutableLiveData<Event<IsNewUser>>()
    val navigate: LiveData<Event<IsNewUser>> = _navigate

    private val _toastFailure = MutableLiveData<Event<Failure>>()
    val toastFailure: LiveData<Event<Failure>> = _toastFailure


    fun submitEmail(email: String) {
        viewModelScope.launch {
            when (val result = isEmailValidForSignIn(email)) {
                is Either.Left -> {
                    when (val failure = result.a) {
                        is SignInFailure.WrongInput -> _email.value = Resource.Error(failure)
                        else -> _toastFailure.value = Event(failure)
                    }
                }
                is Either.Right -> {
                    _email.value = Resource.Success(email)
                    _navigate.value = Event(result.b)
                }
            }
        }
    }

}