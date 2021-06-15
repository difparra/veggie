package com.diegoparra.veggie.user.address.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diegoparra.veggie.core.kotlin.Event
import com.diegoparra.veggie.core.kotlin.Failure
import com.diegoparra.veggie.core.kotlin.Resource
import com.diegoparra.veggie.core.kotlin.toResource
import com.diegoparra.veggie.user.address.domain.Address
import com.diegoparra.veggie.user.address.usecases.DeleteAddressUseCase
import com.diegoparra.veggie.user.address.usecases.SelectMainAddressUseCase
import com.diegoparra.veggie.user.address.usecases.GetAddressUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class AddressViewModel @Inject constructor(
    private val addressUseCase: GetAddressUseCase,
    private val selectMainAddressUseCase: SelectMainAddressUseCase,
    private val deleteAddressUseCase: DeleteAddressUseCase
) : ViewModel() {

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

    fun deleteAddress(addressId: String) {
        viewModelScope.launch {
            try {
                val addressList = addressList.value as Resource.Success
                val address = addressList.data.find { it.id == addressId }!!
                deleteAddress(address)
            } catch (e: Exception) {
                Timber.e("Either addressList is not success, or addressId is not in addressList")
                _failure.value =
                    Event(Failure.ServerError(message = "Ha ocurrido un error inesperado. Intenta de nuevo más tarde."))
            }

        }
    }

    fun selectAddress(addressId: String) {
        viewModelScope.launch {
            selectMainAddressUseCase(addressId)
            _selectedAddressId.value = addressId
        }
    }

}