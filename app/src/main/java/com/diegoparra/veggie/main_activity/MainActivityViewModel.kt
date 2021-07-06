package com.diegoparra.veggie.main_activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.diegoparra.veggie.core.internet.IsInternetAvailableUseCase
import com.diegoparra.veggie.core.internet.SetIsInternetAvailableUseCase

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
        private val getCartSizeUseCase: GetCartSizeUseCase,
        private val isInternetAvailableUseCase: IsInternetAvailableUseCase,
        private val setIsInternetAvailableUseCase: SetIsInternetAvailableUseCase
) : ViewModel() {

    val cartSize = getCartSizeUseCase().asLiveData()

    val isInternetAvailable = isInternetAvailableUseCase().asLiveData()

    fun setInternetAvailable(isInternetAvailable: Boolean) {
        viewModelScope.launch {
            setIsInternetAvailableUseCase(isInternetAvailable)
        }
    }

}