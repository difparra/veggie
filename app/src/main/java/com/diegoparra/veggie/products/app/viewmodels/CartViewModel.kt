package com.diegoparra.veggie.products.app.viewmodels

import androidx.lifecycle.*
import com.diegoparra.veggie.core.Failure
import com.diegoparra.veggie.core.Resource
import com.diegoparra.veggie.products.app.entities.ProductCart
import com.diegoparra.veggie.products.cart.domain.ProductId
import com.diegoparra.veggie.products.app.usecases.GetCartProductsUseCase
import com.diegoparra.veggie.products.app.usecases.GetMinOrderCartUseCase
import com.diegoparra.veggie.products.app.usecases.UpdateQuantityUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val getCartProductsUseCase: GetCartProductsUseCase,
    private val updateQuantityUseCase: UpdateQuantityUseCase,
    private val getMinOrderCartUseCase: GetMinOrderCartUseCase
) : ViewModel() {

    private val _products = MutableStateFlow<Resource<List<ProductCart>>>(Resource.Loading())
    val products: LiveData<Resource<List<ProductCart>>> = _products.asLiveData()

    init {
        viewModelScope.launch {
            getCartProductsUseCase().collect {
                it.fold(::handleFailure, ::handleCartProducts)
            }
        }
    }

    private fun handleCartProducts(products: List<ProductCart>) {
        if (products.isNullOrEmpty()) {
            _products.value = Resource.Error(Failure.CartFailure.EmptyCartList)
        } else {
            //  Must addEditablePositionProperty in here, so that there will always be an item opened to edition
            _products.value = Resource.Success(products.addEditablePositionProperty())
        }
    }

    private fun handleFailure(failure: Failure) {
        _products.value = Resource.Error(failure)
    }


    //      ----------------------------------------------------------------------------------------

    private val _editablePosition = MutableStateFlow(0)
    fun setEditablePosition(position: Int) {
        if (position == _editablePosition.value) {
            return
        }
        _editablePosition.value = position
    }

    init {
        viewModelScope.launch {
            _editablePosition.collect { newEditablePosition ->
                val currentProdsList = products.value
                if (currentProdsList is Resource.Success) {
                    handleCartProducts(currentProdsList.data)
                    if (!currentProdsList.data.isNullOrEmpty()) {
                        val prodListWithEditables =
                            currentProdsList.data.addEditablePositionProperty(newEditablePosition)
                        _products.value = Resource.Success(prodListWithEditables)
                    }
                }
            }
        }
    }

    private fun List<ProductCart>.addEditablePositionProperty(editablePosition: Int = _editablePosition.value): List<ProductCart> {
        if (this.isNullOrEmpty()) {
            return emptyList()
        }
        val validEditablePosition = editablePosition.coerceIn(this.indices)
        return this.mapIndexed { index, product ->
            val editable = index == validEditablePosition
            if (product.isEditable != editable) {
                product.copy(isEditable = editable)
            } else {
                product
            }
        }
    }


    //      ----------------------------------------------------------------------------------------
    //      ----------------------------------------------------------------------------------------


    val clearCartEnabledState: LiveData<Boolean> = _products.map {
        return@map it is Resource.Success && !it.data.isNullOrEmpty()
    }.asLiveData()


    //      ----------------------------------------------------------------------------------------

    private val minOrder = getMinOrderCartUseCase()

    val total = _products.map {
        if (it is Resource.Success) {
            var total = 0
            it.data.forEach {
                total += (it.quantity * it.price)
            }
            if (total < 0) {
                return@map Total.Error
            } else if (total == 0) {
                return@map Total.EmptyCart
            } else if (total < minOrder) {
                return@map Total.MinNotReached(totalValue = total, minOrder = minOrder)
            } else {
                return@map Total.OK(totalValue = total)
            }
        } else if (it is Resource.Error && it.failure is Failure.CartFailure.EmptyCartList) {
            return@map Total.EmptyCart
        } else {
            return@map Total.Error
        }
    }.asLiveData()


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


sealed class Total(val totalValue: Int) {
    class OK(totalValue: Int) : Total(totalValue)
    class MinNotReached(totalValue: Int, val minOrder: Int) : Total(totalValue)
    object EmptyCart : Total(0)
    object Error : Total(0)
}