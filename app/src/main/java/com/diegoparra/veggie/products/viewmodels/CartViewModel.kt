package com.diegoparra.veggie.products.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diegoparra.veggie.core.Failure
import com.diegoparra.veggie.core.Resource
import com.diegoparra.veggie.products.domain.entities.ProductCart
import com.diegoparra.veggie.products.domain.entities.ProductId
import com.diegoparra.veggie.products.domain.usecases.ClearCartListUseCase
import com.diegoparra.veggie.products.domain.usecases.GetCartProductsUseCase
import com.diegoparra.veggie.products.domain.usecases.UpdateQuantityUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
        private val getCartProductsUseCase: GetCartProductsUseCase,
        private val updateQuantityUseCase: UpdateQuantityUseCase,
        private val clearCartListUseCase: ClearCartListUseCase
) : ViewModel() {

    private val _products = MutableLiveData<Resource<List<ProductCart>>>()
    val products : LiveData<Resource<List<ProductCart>>> = _products

    private val _editablePosition = MutableStateFlow(0)
    fun setEditablePosition(position: Int) {
        if(position == _editablePosition.value){
            return
        }
        _editablePosition.value = position
    }

    init {
        viewModelScope.launch {
            _products.value = Resource.Loading()
            getCartProductsUseCase().collect {
                it.fold(::handleFailure, ::handleCartProducts)
            }
        }
        viewModelScope.launch {
            /*
                IMPORTANT NOTE:
                    Every collect {} need to be in a different scope, otherwise that will not work.
                    When I place _editablePosition.collect below getCartProductsUseCase().collect {}
                    and inside the previous viewModelScope.launch {} that didn't work,
                    but placing in different viewModelScope did certainly work.
             */
            _editablePosition.collect { newEditablePosition ->
                val currentProdsList = products.value
                if(currentProdsList is Resource.Success){
                    if(!currentProdsList.data.isNullOrEmpty()){
                        val prodListWithEditables = currentProdsList.data.addEditablePositionProperty(newEditablePosition)
                        _products.value = Resource.Success(prodListWithEditables)
                    }
                }
            }
        }
    }


    //      ----------------------------------------------------------------------------------------

    private fun handleCartProducts(products : List<ProductCart>){
        if(products.isNullOrEmpty()){
            _products.value = Resource.Error(Failure.CartFailure.EmptyCartList)
        }else{
            /*
                Important note:
                Can add visual properties in the viewModel but not modify domain properties,
                such as price, discounts, data in general: This last values need to be handled
                properly in the use cases which can communicate with databases.
             */
            _products.value = Resource.Success(products.addEditablePositionProperty())
        }
    }

    private fun List<ProductCart>.addEditablePositionProperty(editablePosition: Int = _editablePosition.value) : List<ProductCart> {
        val validEditablePosition = editablePosition.coerceIn(this.indices)
        val prodsWithEditables = this.toMutableList()
        prodsWithEditables.forEachIndexed { index, product ->
            val editable = index == validEditablePosition
            if(product.isEditable != editable){
                prodsWithEditables[index] = product.copy(isEditable = editable)
            }
        }
        return prodsWithEditables
    }

    private fun handleFailure(failure: Failure){
        _products.value = Resource.Error(failure)
    }


    //      ----------------------------------------------------------------------------------------

    fun addQuantity(productId: ProductId) {
        val prod = findCartProd(productId)
        prod?.let {
            viewModelScope.launch {
                val maxOrder = it.maxOrder
                updateQuantityUseCase(UpdateQuantityUseCase.Params.Add(productId, maxOrder))
            }
        }// TODO:   Deal when variation was not found in viewModel (could be because of the lifecycles).
    }

    fun reduceQuantity(productId: ProductId) {
        //  Not strictly necessary to findVariation, but better as it is a validation if variation
        //  exists in the products send to show in ui
        val prod = findCartProd(productId)
        prod?.let {
            viewModelScope.launch {
                updateQuantityUseCase(UpdateQuantityUseCase.Params.Reduce(productId))
            }
        }// TODO:   Deal when variation was not found in viewModel (could be because of the lifecycles).
    }

    private fun findCartProd(productId: ProductId) : ProductCart? {
        return when(val prods = products.value){
            is Resource.Success -> prods.data.find { it.productId == productId }
            else -> null
        }
    }


    //      ----------------------------------------------------------------------------------------

    fun clearCartList() {
        viewModelScope.launch {
            clearCartListUseCase()
        }
    }

}