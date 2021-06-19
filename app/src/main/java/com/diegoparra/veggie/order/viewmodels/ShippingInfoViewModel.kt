package com.diegoparra.veggie.order.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.diegoparra.veggie.auth.usecases.GetProfileAsFlowUseCase
import com.diegoparra.veggie.core.kotlin.Either
import com.diegoparra.veggie.core.kotlin.map
import com.diegoparra.veggie.order.domain.DeliverySchedule
import com.diegoparra.veggie.order.usecases.GetDeliveryCostUseCase
import com.diegoparra.veggie.order.usecases.GetDeliveryScheduleOptionsUseCase
import com.diegoparra.veggie.user.address.domain.Address
import com.diegoparra.veggie.user.address.usecases.GetAddressUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ShippingInfoViewModel @Inject constructor(
    private val getDeliveryScheduleOptionsUseCase: GetDeliveryScheduleOptionsUseCase,
    private val getDeliveryCostUseCase: GetDeliveryCostUseCase,
    private val getProfileAsFlowUseCase: GetProfileAsFlowUseCase,
    private val getAddressUseCase: GetAddressUseCase
) : ViewModel() {

    private val _userProfile = getProfileAsFlowUseCase()
    private val _isAuthenticated = _userProfile.map { it is Either.Right }
    val isAuthenticated = _isAuthenticated.asLiveData()

    private val _authenticatedUserProfile =
        _userProfile.filter { it is Either.Right }.map { (it as Either.Right).b }
    private val _userId = _authenticatedUserProfile.map { it.id }
    private val _phoneNumber = _authenticatedUserProfile.map { it.phoneNumber }
    val phoneNumber = _phoneNumber.asLiveData()

    /*  TODO:   Flows like here could make the code cleaner and simpler. I don't have to worry about
                refreshing the data, because it will refresh automatically.
                However, in order to make the code cheaper, calling the database the minimum times,
                I could use a normal state flow, set the initial value on initMethod, and just update
                with a method refreshData, that will be called according to address flow/navgraph result.
     */
    private val _address =
        getAddressUseCase
            .getSelectedAddressAsFlow(currentUserIdAsFlow = _userProfile.map { it.map { it.id } })
            .map { if (it is Either.Right) it.b else null }
    val address = _address.asLiveData()
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
        combine(_address, _deliveryScheduleOptions, _deliverySchedule) { address, deliveryScheduleOptions, selDeliverySchedule ->
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


    fun selectDeliverySchedule(deliverySchedule: DeliverySchedule) {
        _deliverySchedule.value = deliverySchedule
    }


    data class DeliveryCost(
        val schedule: DeliverySchedule,
        val cost: Int,
        val isSelected: Boolean
    )
}