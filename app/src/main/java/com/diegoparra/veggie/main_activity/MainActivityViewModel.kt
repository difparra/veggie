package com.diegoparra.veggie.main_activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
        private val getCartSizeUseCase: GetCartSizeUseCase
) : ViewModel() {

    val cartSize = getCartSizeUseCase().asLiveData()

}