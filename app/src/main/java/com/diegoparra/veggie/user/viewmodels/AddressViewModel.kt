package com.diegoparra.veggie.user.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diegoparra.veggie.address.Address
import com.diegoparra.veggie.address.DeleteAddressUseCase
import com.diegoparra.veggie.address.SelectMainAddressUseCase
import com.diegoparra.veggie.core.*
import com.diegoparra.veggie.user.usecases.GetAddressUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddressViewModel @Inject constructor(
    private val addressUseCase: GetAddressUseCase,
    private val selectMainAddressUseCase: SelectMainAddressUseCase,
    private val deleteAddressUseCase: DeleteAddressUseCase
): ViewModel() {

    private val _addressList = MutableLiveData<Resource<List<Address>>>()
    val addressList: LiveData<Resource<List<Address>>> = _addressList
    private val _selectedAddressId = MutableLiveData<String?>()
    val selectedAddressId: LiveData<String?> = _selectedAddressId

    private val _failure = MutableLiveData<Event<Failure>>()
    val failure: LiveData<Event<Failure>> = _failure

    init {
        refreshData()
    }

    fun refreshData() {
        viewModelScope.launch {
            _addressList.value = Resource.Loading()
            _addressList.value = addressUseCase.getAddressList().toResource()
            _selectedAddressId.value = addressUseCase.getSelectedAddressId()
        }
    }

    fun deleteAddress(address: Address) {
        viewModelScope.launch {
            deleteAddressUseCase(address).fold(
                {
                    _failure.value = Event(it)
                    Unit
                }, {
                    refreshData()
                }
            )
        }
    }

    fun selectAddress(addressId: String) {
        viewModelScope.launch {
            selectMainAddressUseCase(addressId)
            _selectedAddressId.value = addressId
        }
    }

}