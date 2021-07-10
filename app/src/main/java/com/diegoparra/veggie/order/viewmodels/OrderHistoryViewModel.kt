package com.diegoparra.veggie.order.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.diegoparra.veggie.core.internet.IsInternetAvailableUseCase
import com.diegoparra.veggie.core.kotlin.Resource
import com.diegoparra.veggie.core.kotlin.toResource
import com.diegoparra.veggie.order.usecases.GetOrdersListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class OrderHistoryViewModel @Inject constructor(
    private val isInternetAvailableUseCase: IsInternetAvailableUseCase,
    private val getOrdersListUseCase: GetOrdersListUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _orderId = savedStateHandle.getLiveData<String>(ORDER_ID)


    private val _isInternetAvailable = isInternetAvailableUseCase()

    private val _ordersList = _isInternetAvailable
        .map {
            getOrdersListUseCase.forCurrentUser(isInternetAvailable = it).toResource()
        }
        .onStart { emit(Resource.Loading) }
    val ordersList = _ordersList.asLiveData()


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