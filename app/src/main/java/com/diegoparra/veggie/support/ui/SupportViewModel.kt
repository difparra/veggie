package com.diegoparra.veggie.support.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diegoparra.veggie.support.usecases.GetContactEmailUseCase
import com.diegoparra.veggie.support.usecases.GetContactNumberUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SupportViewModel @Inject constructor(
    private val getContactEmailUseCase: GetContactEmailUseCase,
    private val getContactNumberUseCase: GetContactNumberUseCase
): ViewModel() {

    private val _contactEmail = MutableLiveData<String>()
    val contactEmail: LiveData<String> = _contactEmail
    private val _contactNumber = MutableLiveData<String>()
    val contactNumber: LiveData<String> = _contactNumber

    init {
        viewModelScope.launch {
            _contactEmail.value = getContactEmailUseCase.invoke()
        }
        viewModelScope.launch {
            _contactNumber.value = getContactNumberUseCase.invoke()
        }
    }

}