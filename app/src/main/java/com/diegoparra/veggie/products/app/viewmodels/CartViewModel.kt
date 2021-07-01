package com.diegoparra.veggie.products.app.viewmodels

import androidx.lifecycle.*
import com.diegoparra.veggie.core.internet_check.IsInternetAvailableUseCase
import com.diegoparra.veggie.core.kotlin.Failure
import com.diegoparra.veggie.core.kotlin.Resource
import com.diegoparra.veggie.core.kotlin.map
import com.diegoparra.veggie.core.kotlin.toResource
import com.diegoparra.veggie.products.app.entities.ProductCart
import com.diegoparra.veggie.products.cart.domain.ProductId
import com.diegoparra.veggie.products.app.usecases.GetCartProductsUseCase
import com.diegoparra.veggie.order.usecases.GetMinOrderUseCase
import com.diegoparra.veggie.products.app.usecases.UpdateQuantityUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val isInternetAvailableUseCase: IsInternetAvailableUseCase,
    private val getCartProductsUseCase: GetCartProductsUseCase,
    private val updateQuantityUseCase: UpdateQuantityUseCase,
    private val getMinOrderUseCase: GetMinOrderUseCase
) : ViewModel() {

    private val _isInternetAvailable = isInternetAvailableUseCase()
    val isInternetAvailable = _isInternetAvailable.asLiveData()

    private val _editablePosition = MutableStateFlow(0)
    fun setEditablePosition(position: Int) {
        _editablePosition.value = position
    }

    /*
     * By using isInternetAvailable to load products and then using the same variable to enable/disable
     * btnOrder, I can say that products are updated when user try to make the order.
     * The only way btnMakeOrder is enabled is when isInternetAvailable is true, and if it is true,
     * it means products has been recently collected (products will be reloaded on every change
     * of isInternetAvailable).
     */
    private val _products = _isInternetAvailable
        .flatMapLatest {
            getCartProductsUseCase(isInternetAvailable = it)
                .map { it.toResource() }
                .onStart { emit(Resource.Loading()) }
        }
        .combine(_editablePosition) { prodsList, position ->
            //  Map the Resource to add the editable position to the list
            when (prodsList) {
                is Resource.Success ->
                    Resource.Success(addEditablePositionProperty(prodsList.data, position))
                else -> prodsList
            }
        }
    val products = _products.asLiveData()

    private fun addEditablePositionProperty(
        prodsList: List<ProductCart>,
        editablePosition: Int
    ): List<ProductCart> {
        if (prodsList.isNullOrEmpty()) {
            return emptyList()
        }
        val validEditablePosition = editablePosition.coerceIn(prodsList.indices)
        return prodsList.mapIndexed { index, product ->
            val editable = index == validEditablePosition
            if (product.isEditable != editable) {
                product.copy(isEditable = editable)
            } else {
                product
            }
        }
    }


    //      ----------------------------------------------------------------------------------------

    sealed class Total(val totalValue: Int) {
        class OK(totalValue: Int) : Total(totalValue)
        class MinNotReached(totalValue: Int, val minOrder: Int) : Total(totalValue)
        object EmptyCart : Total(0)

        /** When some error has occurred either because list was not correctly loaded or total is <0,
         *  or when products are still being loaded
         */
        object Error : Total(0)
    }

    private val _total = _products.map {
        if (it is Resource.Success) {
            val total = it.data.sumOf { it.quantity * it.price }
            val minOrder = getMinOrderUseCase()     //  This use case already cache the data,
            // and will not call repo every time.
            if (total < 0) {
                return@map Total.Error
            } else if (total == 0) {
                return@map Total.EmptyCart
            } else if (total < minOrder) {
                return@map Total.MinNotReached(totalValue = total, minOrder = minOrder)
            } else {
                return@map Total.OK(totalValue = total)
            }
        } else {
            return@map Total.Error
        }
    }
    val total = _total.asLiveData()


    val clearCartEnabledState: LiveData<Boolean> = _products.map {
        (it is Resource.Success && !it.data.isNullOrEmpty())
    }.asLiveData()

    /*
     *  It is really important to disable btnOrder if there is no internet access.
     *  Otherwise, productData could be not updated.
     */
    val btnMakeOrderEnabled: LiveData<Boolean> =
        combine(_isInternetAvailable, _total) { internetAvailable, total ->
            (internetAvailable && (total is Total.OK))
        }.asLiveData()


    //      ----------------------------------------------------------------------------------------
    //              UI ACTIONS
    //      ----------------------------------------------------------------------------------------

    fun addQuantity(productId: ProductId) {
        val prod = findCartProd(productId)
        prod?.let {
            viewModelScope.launch {
                val maxOrder = it.maxOrder
                updateQuantityUseCase(UpdateQuantityUseCase.Params.Add(productId, maxOrder))
            }
        }
        if (prod == null) {
            Timber.e("Product $productId was not found in CartViewModel products.")
        }
    }

    fun reduceQuantity(productId: ProductId) {
        //  Not strictly necessary to findVariation, but better as it is a validation if variation
        //  exists in the products send to show in ui
        val prod = findCartProd(productId)
        prod?.let {
            viewModelScope.launch {
                updateQuantityUseCase(UpdateQuantityUseCase.Params.Reduce(productId))
            }
        }
        if (prod == null) {
            Timber.e("Product $productId was not found in CartViewModel products.")
        }
    }

    private fun findCartProd(productId: ProductId): ProductCart? {
        return when (val prods = products.value) {
            is Resource.Success -> prods.data.find { it.productId == productId }
            else -> null
        }
    }

}