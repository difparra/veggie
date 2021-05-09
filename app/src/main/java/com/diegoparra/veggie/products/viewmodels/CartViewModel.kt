package com.diegoparra.veggie.products.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diegoparra.veggie.core.Failure
import com.diegoparra.veggie.core.Resource
import com.diegoparra.veggie.products.domain.entities.ProductCart
import com.diegoparra.veggie.products.domain.entities.ProductId
import com.diegoparra.veggie.products.domain.usecases.GetCartProductsUseCase
import com.diegoparra.veggie.products.domain.usecases.UpdateQuantityUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
        private val getCartProductsUseCase: GetCartProductsUseCase,
        private val updateQuantityUseCase: UpdateQuantityUseCase
) : ViewModel() {

    private val _products = MutableLiveData<Resource<List<ProductCart>>>()
    val products : LiveData<Resource<List<ProductCart>>> = _products

    init {
        viewModelScope.launch {
            _products.value = Resource.Loading()
            getCartProductsUseCase().collect {
                it.fold(::handleFailure, ::handleCartProducts)
            }
        }
    }

    private fun handleCartProducts(products : List<ProductCart>){
        if(products.isNullOrEmpty()){
            _products.value = Resource.Error(Failure.CartFailure.EmptyCartList)
        }else{
            _products.value = Resource.Success(products)
        }
    }

    private fun handleFailure(failure: Failure){
        _products.value = Resource.Error(failure)
    }




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

}