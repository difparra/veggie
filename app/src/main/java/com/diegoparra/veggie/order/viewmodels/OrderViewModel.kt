package com.diegoparra.veggie.order.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.diegoparra.veggie.R
import com.diegoparra.veggie.auth.usecases.GetProfileAsFlowUseCase
import com.diegoparra.veggie.auth.utils.AuthFailure
import com.diegoparra.veggie.core.kotlin.*
import com.diegoparra.veggie.core.kotlin.input_validation.InputFailure
import com.diegoparra.veggie.core.kotlin.input_validation.InputFailure.Companion.Field
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

    private val _failure = MutableStateFlow<Event<Failure?>>(Event(null))
    val failure = _failure.asLiveData()


    private val _userProfile = getProfileAsFlowUseCase()

    //  It is better to handle it from here as unique, as some other parts in here like fetching the
    //  address could emit also this failure, and it could be thrown more than once in _failure,
    //  even when the fragment has already listen and get out to signInFlow. That will cause a
    //  crash in navigation. So, the best thing could be handle this failure apart.
    private val _isSignedIn = _userProfile.map { Event(it is Either.Right) }
    val isSignedIn = _isSignedIn.asLiveData()


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

    private val _deliveryScheduleOptions = MutableStateFlow<List<DeliverySchedule>>(emptyList())
    private val _deliverySchedule = MutableStateFlow<DeliverySchedule?>(null)
    val deliverySchedule = _deliverySchedule.asLiveData()
    private val _deliveryCost = MutableStateFlow<Int?>(null)
    val deliveryCost = _deliveryCost.asLiveData()

    init {
        viewModelScope.launch {
            _deliveryScheduleOptions.value = getDeliveryScheduleOptionsUseCase()
        }
    }

    //  Could be only needed deliverySchedule to this function as deliveryCost can be extracted from
    //  _deliveryScheduleAndCost. But this would be an additional operation, and it is almost sure
    //  that ui have this data as I have passed deliveryScheduleAndCost to it.
    fun selectDeliveryScheduleAndCost(deliverySchedule: DeliverySchedule, cost: Int) {
        _deliverySchedule.value = deliverySchedule
        _deliveryCost.value = cost
    }


    data class DeliveryScheduleAndCost(
        val schedule: DeliverySchedule,
        val cost: Int,
        val isSelected: Boolean
    )

    private val _deliveryScheduleAndCosts =
        _deliveryScheduleOptions
            .combine(_address) { options, address ->
                //  Calculate cost based on address
                options.map {
                    DeliveryScheduleAndCost(
                        schedule = it,
                        cost = getDeliveryCostUseCase(it, address),
                        isSelected = false
                    )
                }
            }
            .combine(_deliverySchedule) { options, selected ->
                //  Set the selected address base on the change on _deliverySchedule
                options.map {
                    if (it.schedule == selected) {
                        it.copy(isSelected = true)
                    } else {
                        it
                    }
                }
            }

    val deliveryCosts = _deliveryScheduleAndCosts.asLiveData()


    //      ........................................................................................

    /*
        NOTES ON THIS FLOW:
        - Can be public as it is not mutable and can't be reassigned from ui.
        - It is not really intended to be observed in the ui, but it is necessary to know if it is
          complete at a certain time (when user try to continue to summary fragment).
          -> As value have to have sense at any time, I can't use a simple flow/liveData because
             they are cold and value only makes sense if they have at least one collector/observer.
             But, it can be a hot flow, such as stateFlow, because they will always have a value in
             the defined scope even if they have no observers.
        AdditionalNote: Don't convert to liveData and send to ui, as liveData have the same problem
        as cold flows, value will not make sense until observed.
     */
    val shippingInfo: StateFlow<Either<List<Failure>, ShippingInfo>> =
        combine(
            _userId, _phoneNumber, _address, _deliverySchedule, _deliveryCost
        ) { userId, phoneNumber, address, deliverySchedule, deliveryCost ->
            Timber.d("_shippingInfo combine called.")
            //  Validation
            val validation = validateShippingInfoComplete(
                userId, phoneNumber, address, deliverySchedule, deliveryCost
            )

            //  Returning result
            when (validation) {
                is Either.Left -> Either.Left(validation.a)
                is Either.Right ->
                    Either.Right(
                        //  Values should be checked not null in
                        ShippingInfo(
                            userId = userId!!,
                            phoneNumber = phoneNumber!!,
                            address = address!!,
                            deliverySchedule = deliverySchedule!!,
                            deliveryCost = deliveryCost!!
                        )
                    )
            }
        }.stateIn(
            viewModelScope, SharingStarted.Eagerly, Either.Left(
                listOf(Failure.ClientError(message = "Shipping info has not been initialized or is incomplete"))
            )
        )


    private fun validateShippingInfoComplete(
        userId: String?,
        phoneNumber: String?,
        address: Address?,
        deliverySchedule: DeliverySchedule?,
        deliveryCost: Int?
    ): Either<List<Failure>, Unit> {
        if (userId == null) {
            return Either.Left(listOf(AuthFailure.SignInState.NotSignedIn))
        }
        val failures: MutableList<Failure> = mutableListOf()
        if (phoneNumber == null) {
            failures.add(InputFailure.Empty(field = Field.PHONE_NUMBER, ""))
        }
        if (address == null) {
            failures.add(InputFailure.Empty(field = Field.ADDRESS, ""))
        }
        if (deliverySchedule == null || deliveryCost == null) {
            failures.add(InputFailure.Empty(field = Field.OTHER("deliveryDateTime", R.string.schedule_your_delivery, true), ""))
        }
        if (failures.isNotEmpty()) {
            return Either.Left(failures)
        } else {
            return Either.Right(Unit)
        }
    }


    /*
     *      PRODUCTS LIST       --------------------------------------------------------------------
     */

    private val _productsList = MutableStateFlow<ProductsList?>(null)
    /*
        Notes about productsList:
        - In this case it is better to use stateFlow than liveData, because:
            - With stateFlow I can have a consistent value scoped to the viewModel, which is
              important when I want to send order.
            - When I want to send order, productsList must have a value, otherwise it will throw a
              failure and if I used liveData, and user hasn't attached observer to the liveData (if
              they haven't opened ProductsOrderSummaryFragment, which is perfectly possible), data
              will be null.
                -----       FOR THIS REASON AS LIVE DATA MUST BE USED WITH CAUTION.     -----
            - It is also possible to still use as liveData, but must be used with caution and in
              sendOrder and prevalidateFieldsSendOrder functions, MUST be used the private _productsList
              as it will certainly have a value but liveData not, as described previously.
              In order to have a clean code, it is better not to have those limitations and use
              productsList asStateFlow.
     */
    val productsList = _productsList.asStateFlow()

    init {
        viewModelScope.launch {
            getProductsListOrder().fold(
                { _failure.value = Event(it) },
                { _productsList.value = it }
            )
        }
    }

    //  Be careful, it will not have data until _deliveryCost has also emitted
    val total = combine(
        _productsList,
        _deliveryCost
    ) { prodsList, deliveryCost ->
        if (prodsList == null || deliveryCost == null) {
            return@combine null
        }
        Total(
            subtotal = prodsList.subtotal(),
            additionalDiscounts = 0,
            deliveryCost = deliveryCost,
            tip = 0
        )
    }.asLiveData()


    private val _paymentInfo = MutableStateFlow<PaymentInfo>(
        PaymentInfo(
            status = PaymentStatus.PENDING,
            paymentMethod = PaymentMethod.CASH,
            details = "Pago contraentrega",
            additionalInfo = mapOf()
        )
    )


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
                        shippingInfo = shippingInfo.value.getOrNull()!!,
                        products = productsList.value!!,
                        total = total.value!!,
                        paymentInfo = _paymentInfo.value,
                        additionalNotes = null
                    )
                    _sendOrderResult.value = Event(result.toResource())
                }
            }
        }
    }

    private fun prevalidateFieldsSendOrder(): Either<Failure, Unit> {
        if (shippingInfo.value is Failure) {
            return Either.Left(Failure.ClientError(message = "Shipping info is not complete"))
        } else if (productsList.value == null || productsList.value?.products.isNullOrEmpty()) {
            return Either.Left(Failure.ClientError(message = "Products list is null or empty"))
        } else if (total.value == null || total.value?.total == 0) {
            return Either.Left(Failure.ClientError(message = "Total is null or 0."))
        } else if (_paymentInfo.value == null) {
            return Either.Left(Failure.ClientError(message = "Payment info has not been selected"))
        } else {
            return Either.Right(Unit)
        }
    }


}