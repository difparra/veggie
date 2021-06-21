package com.diegoparra.veggie.user.address.viewmodels

import androidx.lifecycle.*
import com.diegoparra.veggie.core.kotlin.*
import com.diegoparra.veggie.user.address.domain.Address
import com.diegoparra.veggie.user.address.usecases.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddressListViewModel @Inject constructor(
    private val getAddressListUseCase: GetAddressListUseCase,
    private val getSelectedAddressUseCase: GetSelectedAddressUseCase,
    private val selectMainAddressUseCase: SelectMainAddressUseCase,
    private val deleteAddressUseCase: DeleteAddressUseCase
) : ViewModel() {

    private val _failure = MutableLiveData<Event<Failure>>()
    val failure: LiveData<Event<Failure>> = _failure


    /*
     *      ADDRESS LIST        --------------------------------------------------------------------
     */

    private val _addressList = MutableStateFlow<Resource<List<Address>>>(Resource.Loading())
    val addressList: LiveData<Resource<List<Address>>> = _addressList.asLiveData()

    init {
        refreshAddressList()
    }

    /**
     * Could get flows from repo, but setting it manually calls repo the minimum making the app
     * faster and cheaper.
     */
    fun refreshAddressList() {
        viewModelScope.launch {
            _addressList.value = getAddressListUseCase().toResource()
        }
    }


    /*
     *      SELECTED ADDRESS        ----------------------------------------------------------------
     */

    private val _selectedAddress = MutableStateFlow<Address?>(null)
    val selectedAddress = _selectedAddress.asLiveData()
    init {
        /*
         *  I tried defining _selectedAddress by mapping the _addressList.
         *  But that way, the mapping resulted in flow, which I couldn't modify with .value
         *  in selectAddress(). So, the best way would still be define the _selectedAddress
         *  as mutableStateFlow, and observe the _addressList in the viewModelScope, when
         *  addressList changed, changed the _selectedAddress manually with .value
         */
        viewModelScope.launch {
            _addressList.collect {
                if (it is Resource.Success) {
                    val selectedAddress = getSelectedAddressUseCase(addressList = it.data).getOrElse(null)
                    //  Check the first one if none is selected.
                    _selectedAddress.value = selectedAddress ?: selectFirstAddress(it.data)
                } else {
                    _selectedAddress.value = null
                }
            }
        }
    }

    private fun selectFirstAddress(addressList: List<Address>): Address? {
        if (addressList.isEmpty()) {
            return null
        } else {
            val address = addressList.first()
            selectAddress(addressId = address.id)
            return address
        }
    }


    /*
     *      MODIFYING ADDRESS LIST        ----------------------------------------------------------
     */

    private fun searchAddressById(addressId: String): Address? {
        return when (val addressList = addressList.value) {
            is Resource.Success -> addressList.data.find { it.id == addressId }
            else -> null
        }
    }

    fun deleteAddress(address: Address) {
        viewModelScope.launch {
            deleteAddressUseCase(address).fold(
                { _failure.value = Event(it) },
                { refreshAddressList() }
            )
        }
    }

    fun deleteAddress(addressId: String) {
        val address = searchAddressById(addressId)
        address?.let { deleteAddress(it) }
    }

    fun selectAddress(addressId: String) {
        viewModelScope.launch {
            selectMainAddressUseCase(addressId)
            _selectedAddress.value = searchAddressById(addressId)
        }
    }

}