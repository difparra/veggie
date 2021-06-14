package com.diegoparra.veggie.user.address.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diegoparra.veggie.core.Event
import com.diegoparra.veggie.core.Failure
import com.diegoparra.veggie.core.Fields
import com.diegoparra.veggie.core.SignInFailure
import com.diegoparra.veggie.user.address.usecases.SaveAddressUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddressAddViewModel @Inject constructor(
    private val saveAddressUseCase: SaveAddressUseCase
) : ViewModel() {

    private val _navigateSuccess = MutableLiveData<Event<Boolean>>()
    val navigateSuccess: LiveData<Event<Boolean>> = _navigateSuccess

    private val _failure = MutableLiveData<Event<Failure>>()
    val failure: LiveData<Event<Failure>> = _failure

    private val _addressFailure = MutableLiveData<Event<SignInFailure.WrongInput>>()
    val addressFailure: LiveData<Event<SignInFailure.WrongInput>> = _addressFailure


    fun saveAddress(address: String, details: String, instructions: String) {
        viewModelScope.launch {
            saveAddressUseCase(address, details, instructions).fold(
                {
                    when (it) {
                        is SignInFailure.WrongInput -> {
                            if (it.field == Fields.ADDRESS) {
                                _addressFailure.value = Event(it)
                            } else {
                                _failure.value = Event(it)
                            }
                        }
                        else -> _failure.value = Event(it)
                    }
                }, {
                    _navigateSuccess.value = Event(true)
                    Unit
                }
            )
        }
    }

}