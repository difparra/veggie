package com.diegoparra.veggie.order.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.diegoparra.veggie.core.kotlin.Resource
import com.diegoparra.veggie.core.kotlin.toResource
import com.diegoparra.veggie.order.domain.Order
import com.diegoparra.veggie.order.usecases.GetOrdersListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrdersHistoryViewModel @Inject constructor(
    private val getOrdersListUseCase: GetOrdersListUseCase
) : ViewModel() {

    private val _ordersList: MutableStateFlow<Resource<List<Order>>> =
        MutableStateFlow(Resource.Loading())
    val ordersList = _ordersList.asLiveData()

    init {
        viewModelScope.launch {
            _ordersList.value = getOrdersListUseCase.forCurrentUser().toResource()
        }
    }

}