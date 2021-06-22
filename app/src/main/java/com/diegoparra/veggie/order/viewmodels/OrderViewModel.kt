package com.diegoparra.veggie.order.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.diegoparra.veggie.auth.usecases.GetProfileAsFlowUseCase
import com.diegoparra.veggie.auth.utils.AuthFailure
import com.diegoparra.veggie.auth.utils.Fields
import com.diegoparra.veggie.core.kotlin.*
import com.diegoparra.veggie.order.domain.DeliverySchedule
import com.diegoparra.veggie.order.domain.ShippingInfo
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
class OrderViewModel @Inject constructor(
    private val getProfileAsFlowUseCase: GetProfileAsFlowUseCase,
    private val getSelectedAddressUseCase: GetSelectedAddressUseCase,
    private val getDeliveryScheduleOptionsUseCase: GetDeliveryScheduleOptionsUseCase,
    private val getDeliveryCostUseCase: GetDeliveryCostUseCase
) : ViewModel() {

    companion object Fields {
        const val ADDRESS = com.diegoparra.veggie.auth.utils.Fields.ADDRESS
        const val PHONE_NUMBER = com.diegoparra.veggie.auth.utils.Fields.PHONE_NUMBER
        const val DELIVERY_DATE_TIME = "deliveryDateTime"
    }


    private val _userProfile = getProfileAsFlowUseCase()
    val isSignedIn = _userProfile.map { Event(it is Either.Right) }.asLiveData()


    /*
     *      SHIPPING INFO       --------------------------------------------------------------------
     */

    private val _userId = _userProfile.map { it.getOrElse(null)?.id }
    val userId = _userId.asLiveData()
    private val _phoneNumber = _userProfile.map { it.getOrElse(null)?.phoneNumber }
    val phoneNumber = _phoneNumber.asLiveData()

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

    data class DeliveryScheduleAndCost(
        val schedule: DeliverySchedule,
        val cost: Int,
        val isSelected: Boolean
    )

    private val _deliveryScheduleAndCosts =
        combine(
            _address,
            _deliveryScheduleOptions,
            _deliverySchedule
        ) { address, deliveryScheduleOptions, selDeliverySchedule ->
            Timber.d("_deliveryCost combine flow called with address = ${address?.fullAddress()}, deliveryScheduleOptions = $deliveryScheduleOptions")
            deliveryScheduleOptions.map {
                DeliveryScheduleAndCost(
                    schedule = it,
                    cost = getDeliveryCostUseCase(it, address),
                    isSelected = it == selDeliverySchedule
                )
            }
        }
    val deliveryCosts = _deliveryScheduleAndCosts.asLiveData()


    fun selectDeliverySchedule(deliverySchedule: DeliverySchedule, cost: Int) {
        _deliverySchedule.value = deliverySchedule
        _deliveryCost.value = cost
    }

    private val _shippingInfo = combine(
        _userId, _phoneNumber, _address, _deliverySchedule, _deliveryCost
    ) { userId, phoneNumber, address, deliverySchedule, deliveryCost ->

        //  Validation
        if (userId == null) {
            return@combine Either.Left(listOf(AuthFailure.SignInState.NotSignedIn))
        }
        val failures: MutableList<Failure> = mutableListOf()
        if (phoneNumber == null) {
            failures.add(AuthFailure.WrongInput.Empty(field = PHONE_NUMBER, ""))
        }
        if (address == null) {
            failures.add(AuthFailure.WrongInput.Empty(field = ADDRESS, ""))
        }
        if (deliverySchedule == null || deliveryCost == null) {
            failures.add(AuthFailure.WrongInput.Empty(field = DELIVERY_DATE_TIME, ""))
        }

        //  Returning value
        if (failures.isNotEmpty()) {
            return@combine Either.Left(failures)
        } else {
            return@combine Either.Right(
                ShippingInfo(
                    userId = userId,
                    phoneNumber = phoneNumber!!,
                    address = address!!,
                    deliverySchedule = deliverySchedule!!,
                    deliveryCost = deliveryCost!!
                )
            )
        }
    }
    val shippingInfo = _shippingInfo.asLiveData()

}