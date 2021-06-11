package com.diegoparra.veggie.user.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diegoparra.veggie.core.Either
import com.diegoparra.veggie.core.Failure
import com.diegoparra.veggie.core.Resource
import com.diegoparra.veggie.core.toResource
import com.diegoparra.veggie.user.domain.Address
import com.diegoparra.veggie.user.usecases.GetUserDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddressViewModel @Inject constructor(
    private val userDataUseCase: GetUserDataUseCase
): ViewModel() {

    private val _addressList = MutableLiveData<Resource<List<Address>>>()
    val addressList: LiveData<Resource<List<Address>>> = _addressList

    init {
        viewModelScope.launch {
            _addressList.value = Resource.Loading()
            _addressList.value = userDataUseCase.getAddressList().toResource()
        }
    }

}