package com.diegoparra.veggie.order.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.diegoparra.veggie.auth.usecases.GetProfileAsFlowUseCase
import com.diegoparra.veggie.auth.utils.AuthFailure
import com.diegoparra.veggie.core.kotlin.*
import com.diegoparra.veggie.order.domain.*
import com.diegoparra.veggie.order.usecases.GetDeliveryCostUseCase
import com.diegoparra.veggie.order.usecases.GetDeliveryScheduleOptionsUseCase
import com.diegoparra.veggie.order.usecases.GetProductsListOrder
import com.diegoparra.veggie.order.usecases.SendOrderUseCase
import com.diegoparra.veggie.user.address.domain.Address
import com.diegoparra.veggie.user.address.usecases.GetSelectedAddressUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val getProfileAsFlowUseCase: GetProfileAsFlowUseCase,
    private val getSelectedAddressUseCase: GetSelectedAddressUseCase,
    private val getDeliveryScheduleOptionsUseCase: GetDeliveryScheduleOptionsUseCase,
    private val getDeliveryCostUseCase: GetDeliveryCostUseCase,
    private val getProductsListOrder: GetProductsListOrder,
    private val sendOrderUseCase: SendOrderUseCase
) : ViewModel() {

    /*
        TODO:
            Clean the code by using state / hot flows observed in viewModel which are
            not actually needed to be observed in fragments.
            Check:
                https://stackoverflow.com/questions/66883453/sharing-a-cold-flow-of-mutablestateflow-in-viewmodel-between-two-fragments

        TODO General:
            Improved viewModels by removing some unnecessary init methods.
            I used init when a resource was involved in order to set loading state, but loading
            state can be set using onStart or onEach property in flows.
     */

    companion object Fields {
        const val ADDRESS = com.diegoparra.veggie.auth.utils.Fields.ADDRESS
        const val PHONE_NUMBER = com.diegoparra.veggie.auth.utils.Fields.PHONE_NUMBER
        const val DELIVERY_DATE_TIME = "deliveryDateTime"
    }

    private val _failure = MutableStateFlow<Event<Failure?>>(Event(null))
    val failure = _failure.asLiveData()


    private val _userProfile = getProfileAsFlowUseCase()

    //  It is better to handle it from here as unique, as some other parts in here like fetching the
    //  address could emit also this failure, and it could be thrown more than once in _failure,
    //  even when the fragment has already listen and get out to signInFlow. That will cause a
    //  crash in navigation. So, the best thing could be handle this failure apart.
    private val _isSignedIn = _userProfile.map { Event(it is Either.Right) }
    val isSignedIn = _isSignedIn.asLiveData()

    init {
        viewModelScope.launch {
            _userProfile.collect {
                if (it is Either.Left) {
                    _failure.value = Event(it.a)
                }
            }
        }
    }


    /*
     *      SHIPPING INFO       --------------------------------------------------------------------
     */

    private val _userId = _userProfile.map { it.getOrNull()?.id }
    val userId = _userId.asLiveData()
    private val _name = _userProfile.map { it.getOrNull()?.name }
    val name = _name.asLiveData()
    private val _phoneNumber = _userProfile.map { it.getOrNull()?.phoneNumber }
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
            //  Should check that this method being called from addressResult stateHandleListener is not being called
            //  more than once, when user keep selecting different addresses while in nav_address flow/graph.
            Timber.d("refreshAddress() called")
            getSelectedAddressUseCase().fold(
                { _failure.value = Event(it) },
                { _address.value = it }
            //  Could throw a singInState failure, but is ok as it will not be repeated because the only
            //  one generating this failure is signInState.
            //  When handling the failure in fragment, do not handle it with navigation.
            )
        }
    }

    private val _deliverySchedule = MutableStateFlow<DeliverySchedule?>(null)
    val deliverySchedule = _deliverySchedule.asLiveData()
    private val _deliveryCost = MutableStateFlow<Int?>(null)
    val deliveryCost = _deliveryCost.asLiveData()
    private val _deliveryScheduleOptions = MutableStateFlow<List<DeliverySchedule>>(emptyList())

    init {
        viewModelScope.launch {
            _deliveryScheduleOptions.value = getDeliveryScheduleOptionsUseCase()
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


    /*
     *      PRODUCTS LIST       --------------------------------------------------------------------
     */

    private val _productsList = MutableStateFlow<ProductsList?>(null)
    val productsList = _productsList.asLiveData()

    init {
        viewModelScope.launch {
            getProductsListOrder().fold(
                { _failure.value = Event(it) },
                { _productsList.value = it }
            )
        }
    }

    val total = combine(
        _productsList,
        _deliveryCost
    ) { prodsList, deliveryCost ->
        if (prodsList == null || deliveryCost == null) {
            return@combine null
        }
        Total(
            productsBeforeDiscount = prodsList.totalBeforeDiscounts(),
            productsDiscount = prodsList.totalDiscounts(),
            subtotal = prodsList.subtotal(),
            additionalDiscounts = 0,
            deliveryCost = deliveryCost,
            tip = 0
        )
    }.asLiveData()


    /*
     *      SEND ORDER       -----------------------------------------------------------------------
     */

    private val _sendOrderResult = MutableStateFlow<Event<Resource<String>?>>(Event(null))
    val sendOrderResult = _sendOrderResult.asLiveData()

    fun sendOrder() {
        viewModelScope.launch {
            when (val prevalidation = prevalidateFieldsSendOrder()) {
                is Either.Left -> {
                    _failure.value = Event(prevalidation.a)
                    return@launch
                }
                is Either.Right -> {
                    _sendOrderResult.value = Event(Resource.Loading())
                    val result = sendOrderUseCase(
                        shippingInfo = shippingInfo.value!!.getOrNull()!!,
                        products = _productsList.value!!,
                        total = total.value!!,
                        paymentInfo = sendOrderUseCase.createPaymentInfo(
                            paymentStatus = PaymentStatus.PENDING,
                            paymentMethod = PaymentMethod.Cash(null),
                            total = total.value!!.total.toDouble(),
                            paidAt = null
                        ),
                        additionalNotes = null
                    )
                    _sendOrderResult.value = Event(result.toResource())
                }
            }
        }
    }

    private fun prevalidateFieldsSendOrder(): Either<Failure, Unit> {
        if (shippingInfo.value == null || shippingInfo.value is Failure) {
            return Either.Left(Failure.ClientError(message = "Shipping info is not complete"))
        } else if (productsList.value == null || productsList.value?.products.isNullOrEmpty()) {
            return Either.Left(Failure.ClientError(message = "Products list is null or empty"))
        } else if (total.value == null || total.value?.total == 0) {
            return Either.Left(Failure.ClientError(message = "Total is null or 0."))
        } else {
            return Either.Right(Unit)
        }
    }


}