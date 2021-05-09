package com.diegoparra.veggie.products.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diegoparra.veggie.core.Failure
import com.diegoparra.veggie.core.Resource
import com.diegoparra.veggie.products.domain.entities.ProductCart
import com.diegoparra.veggie.products.domain.usecases.GetCartProductsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
        val getCartProductsUseCase: GetCartProductsUseCase
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

}