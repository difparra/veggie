package com.diegoparra.veggie.products.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diegoparra.veggie.products.usecases.ClearCartListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClearCartViewModel @Inject constructor(
        private val clearCartListUseCase: ClearCartListUseCase
) : ViewModel() {

    fun clearCartList() {
        viewModelScope.launch {
            clearCartListUseCase()
        }
    }
}