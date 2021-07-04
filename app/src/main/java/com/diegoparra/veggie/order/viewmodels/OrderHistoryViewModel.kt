package com.diegoparra.veggie.order.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.diegoparra.veggie.core.kotlin.Resource
import com.diegoparra.veggie.core.kotlin.toResource
import com.diegoparra.veggie.order.domain.Order
import com.diegoparra.veggie.order.usecases.GetOrdersListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderHistoryViewModel @Inject constructor(
    private val getOrdersListUseCase: GetOrdersListUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _orderId = savedStateHandle.getLiveData<String>(ORDER_ID)


    private val _ordersList: MutableStateFlow<Resource<List<Order>>> =
        MutableStateFlow(Resource.Loading())
    val ordersList = _ordersList.asLiveData()

    init {
        viewModelScope.launch {
            _ordersList.value = getOrdersListUseCase.forCurrentUser().toResource()
        }
    }


    val selectedOrder = _ordersList
        .map {
            if (it is Resource.Success) {
                it.data
            } else {
                null
            }
        }
        .map { it?.find { it.id == _orderId.value } }
        .filterNotNull()
        .asLiveData()


    fun selectOrder(orderId: String) {
        savedStateHandle.set(ORDER_ID, orderId)
    }


    companion object {
        private const val ORDER_ID = "order_id"
    }

}