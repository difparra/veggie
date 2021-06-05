package com.diegoparra.veggie.user.viewmodels

import androidx.lifecycle.*
import com.diegoparra.veggie.core.*
import com.diegoparra.veggie.user.usecases.GetUserDataUseCase
import com.diegoparra.veggie.user.usecases.SaveProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val getUserDataUseCase: GetUserDataUseCase,
    private val saveProfileUseCase: SaveProfileUseCase
) : ViewModel() {

    val initialEmail =
        getUserDataUseCase.getEmail().map { if (it is Either.Right) Event(it.b) else Event(null) }
            .asLiveData()

    val initialName =
        getUserDataUseCase.getName().map { if (it is Either.Right) Event(it.b) else Event(null) }
            .asLiveData()


    private val _initialPhoneNumber = MutableLiveData<Event<String?>>()
    val initialPhoneNumber: LiveData<Event<String?>> = _initialPhoneNumber
    init {
        viewModelScope.launch {
            getUserDataUseCase.getPhoneNumber().fold(
                { _initialPhoneNumber.value = Event(null); Unit },
                { _initialPhoneNumber.value = Event(it); Unit }
            )
        }
    }

    private val _name = MutableStateFlow<Resource<String>>(Resource.Success(""))
    val name = _name.asLiveData()

    private val _failure = MutableLiveData<Event<Failure>>()
    val failure: LiveData<Event<Failure>> = _failure

    private val _navigateSuccess = MutableLiveData<Event<Boolean>>()
    val navigateSuccess: LiveData<Event<Boolean>> = _navigateSuccess


    fun setName(name: String) {
        _name.value = saveProfileUseCase.validateName(name).toResource()
    }

    fun save(name: String) {
        saveProfileUseCase.saveName(name).fold(
            ::handleFailure,
            ::handleSuccess
        )
    }

    private fun handleFailure(failure: Failure) {
        when (failure) {
            is SignInFailure.WrongInput -> _name.value = Resource.Error(failure)
            else -> _failure.value = Event(failure)
        }
    }

    private fun handleSuccess(nothing: Unit){
        _navigateSuccess.value = Event(true)
    }

}