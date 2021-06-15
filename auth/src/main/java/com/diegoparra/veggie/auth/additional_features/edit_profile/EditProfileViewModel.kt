package com.diegoparra.veggie.auth.additional_features.edit_profile

import androidx.lifecycle.*
import com.diegoparra.veggie.auth.usecases.GetProfileUseCase
import com.diegoparra.veggie.auth.usecases.SaveNameUseCase
import com.diegoparra.veggie.auth.utils.AuthFailure
import com.diegoparra.veggie.core.kotlin.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val getProfileUseCase: GetProfileUseCase,
    private val saveNameUseCase: SaveNameUseCase
) : ViewModel() {

    private val _initialEmail = MutableLiveData(Event(""))
    val initialEmail: LiveData<Event<String>> = _initialEmail

    private val _initialName = MutableLiveData(Event(""))
    val initialName: LiveData<Event<String>> = _initialName

    private val _initialPhoneNumber = MutableLiveData(Event(""))
    val initialPhoneNumber: LiveData<Event<String>> = _initialPhoneNumber


    init {
        refreshData(email = true, name = true, phoneNumber = true)
    }

    fun refreshData(email: Boolean = false, name: Boolean = false, phoneNumber: Boolean = false) {
        viewModelScope.launch {
            getProfileUseCase.getProfile().fold({}, {
                if (!email && !name && !phoneNumber) {
                    _initialEmail.value = Event(it.email)
                    _initialName.value = Event(it.name)
                    _initialPhoneNumber.value = Event(it.phoneNumber ?: "")
                } else {
                    email.runIfTrue { _initialEmail.value = Event(it.email) }
                    name.runIfTrue { _initialName.value = Event(it.name) }
                    phoneNumber.runIfTrue {
                        _initialPhoneNumber.value = Event(it.phoneNumber ?: "")
                    }
                }
                Unit
            })
        }
    }


    private val _name = MutableStateFlow<Resource<String>>(Resource.Success(""))
    val name = _name.asLiveData()

    private val _failure = MutableLiveData<Event<Failure>>()
    val failure: LiveData<Event<Failure>> = _failure

    private val _navigateSuccess = MutableLiveData<Event<Boolean>>()
    val navigateSuccess: LiveData<Event<Boolean>> = _navigateSuccess

    fun setName(name: String) {
        _name.value = saveNameUseCase.validateName(name).toResource()
    }

    val nameIsChanged = _name.map {
        !(it is Resource.Success && it.data == _initialName.value?.peekContent())
    }.distinctUntilChanged().asLiveData()

    fun save(name: String) {
        viewModelScope.launch {
            saveNameUseCase.saveName(name).fold(
                ::handleFailure,
                ::handleSuccess
            )
        }
    }

    private fun handleFailure(failure: Failure) {
        when (failure) {
            is AuthFailure.WrongInput -> _name.value = Resource.Error(failure)
            else -> _failure.value = Event(failure)
        }
    }

    private fun handleSuccess(nothing: Unit) {
        _navigateSuccess.value = Event(true)
    }

}