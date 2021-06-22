package com.diegoparra.veggie.order.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.diegoparra.veggie.auth.usecases.GetProfileAsFlowUseCase
import com.diegoparra.veggie.core.kotlin.Either
import com.diegoparra.veggie.core.kotlin.getOrElse
import com.diegoparra.veggie.core.kotlin.map
import com.diegoparra.veggie.order.domain.DeliverySchedule
import com.diegoparra.veggie.order.usecases.GetDeliveryCostUseCase
import com.diegoparra.veggie.order.usecases.GetDeliveryScheduleOptionsUseCase
import com.diegoparra.veggie.user.address.domain.Address
import com.diegoparra.veggie.user.address.usecases.GetSelectedAddressUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ShippingInfoViewModel @Inject constructor(
    private val getProfileAsFlowUseCase: GetProfileAsFlowUseCase,
    private val getSelectedAddressUseCase: GetSelectedAddressUseCase,
    private val getDeliveryScheduleOptionsUseCase: GetDeliveryScheduleOptionsUseCase,
    private val getDeliveryCostUseCase: GetDeliveryCostUseCase
) : ViewModel() {

    private val _userProfile = getProfileAsFlowUseCase()
    val isAuthenticated = _userProfile.map { it is Either.Right }.asLiveData()

    private val _userId = _userProfile.map { it.map { it.id } }
    val userId = _userId.map { it.getOrElse(null) }.asLiveData()
    private val _phoneNumber = _userProfile.map { it.map { it.phoneNumber } }
    val phoneNumber = _phoneNumber.map { it.getOrElse(null) }.asLiveData()

    //  phoneNumber can change while viewModel is alive, but as it is a flow, I don't need to
    //  implement a function refreshPhoneNumber

    private val _address = MutableStateFlow<Address?>(null)
    val address = _address.asLiveData()

    init {
        refreshAddress()
    }

    fun refreshAddress() {
        viewModelScope.launch {
            //  Should check that this method being called from addressResult stateHandleListener is
            //  not being called more than once, when user keep selecting different addresses while
            //  in nav_address flow/graph.
            Timber.d("refreshAddress() called")
            _address.value = getSelectedAddressUseCase().fold<Address?>({ null }, { it })
        }
    }

    private val _deliverySchedule = MutableStateFlow<DeliverySchedule?>(null)
    private val _deliveryCost = MutableStateFlow<Int?>(null)
    private val _deliveryScheduleOptions = MutableStateFlow<List<DeliverySchedule>>(emptyList())

    init {
        viewModelScope.launch {
            val scheduleOptions = getDeliveryScheduleOptionsUseCase()
            _deliveryScheduleOptions.value = scheduleOptions
        }
    }


    private val _deliveryCosts =
        combine(
            _address,
            _deliveryScheduleOptions,
            _deliverySchedule
        ) { address, deliveryScheduleOptions, selDeliverySchedule ->
            Timber.d("_deliveryCost combine flow called with address = ${address?.fullAddress()}, deliveryScheduleOptions = $deliveryScheduleOptions")
            deliveryScheduleOptions.map {
                DeliveryCost(
                    schedule = it,
                    cost = getDeliveryCostUseCase(it, address),
                    isSelected = it == selDeliverySchedule
                )
            }
        }
    val deliveryCosts = _deliveryCosts.asLiveData()


    fun selectDeliverySchedule(deliverySchedule: DeliverySchedule, cost: Int) {
        _deliverySchedule.value = deliverySchedule
        _deliveryCost.value = cost
    }

    /*  //  Could have been used, but for simplicity, performance and due to the fact that the ui
        //  design allows it, I could ask for the cost in the function as well, avoiding the search
        //  into the flow.
    fun selectDeliverySchedule(deliverySchedule: DeliverySchedule) {
        _deliverySchedule.value = deliverySchedule
        viewModelScope.launch {
            _deliveryCost.value = _deliveryCosts.first()
                .find{ it.schedule == _deliverySchedule.value }!!
                .cost
        }
    }*/


    data class DeliveryCost(
        val schedule: DeliverySchedule,
        val cost: Int,
        val isSelected: Boolean
    )
}