package com.diegoparra.veggie.products.app.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diegoparra.veggie.products.app.usecases.ClearCartListUseCase
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