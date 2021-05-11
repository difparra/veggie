package com.diegoparra.veggie.products.viewmodels

import androidx.lifecycle.*
import com.diegoparra.veggie.core.Failure
import com.diegoparra.veggie.core.Resource
import com.diegoparra.veggie.products.domain.entities.ProductCart
import com.diegoparra.veggie.products.domain.entities.ProductId
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
            //  Must addEditablePositionProperty in here, so that there will always be an item opened to edition
            _products.value = Resource.Success(products.addEditablePositionProperty())
        }
    }

    private fun handleFailure(failure: Failure){
        _products.value = Resource.Error(failure)
    }


    //      ----------------------------------------------------------------------------------------

    private val _editablePosition = MutableStateFlow(0)
    fun setEditablePosition(position: Int) {
        if(position == _editablePosition.value){
            return
        }
        _editablePosition.value = position
    }

    init {
        viewModelScope.launch {
            _editablePosition.collect { newEditablePosition ->
                val currentProdsList = products.value
                if(currentProdsList is Resource.Success){
                    handleCartProducts(currentProdsList.data)
                    if(!currentProdsList.data.isNullOrEmpty()){
                        val prodListWithEditables = currentProdsList.data.addEditablePositionProperty(newEditablePosition)
                        _products.value = Resource.Success(prodListWithEditables)
                    }
                }
            }
        }
    }

    private fun List<ProductCart>.addEditablePositionProperty(editablePosition: Int = _editablePosition.value) : List<ProductCart> {
        if(this.isNullOrEmpty()) { return emptyList() }
        val validEditablePosition = editablePosition.coerceIn(this.indices)
        return this.mapIndexed { index, product ->
            val editable = index == validEditablePosition
            if(product.isEditable != editable){
                product.copy(isEditable = editable)
            }else{
                product
            }
        }
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

}


/*
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
            //setEditablePosition(_editablePosition.value.coerceIn(products.indices))
            forceEditablePositionOnProductsListSizeChange(products)
        }
    }

    private var lastProdsSize = 0
    fun forceEditablePositionOnProductsListSizeChange(products: List<ProductCart>){
        if(products.size != lastProdsSize){
            Timber.d("products.size = ${products.size}, lastProdSize = $lastProdsSize")
            _editablePosition.value = _editablePosition.value?.coerceIn(products.indices) ?: 0
            lastProdsSize = products.size
        }
    }


    private fun handleFailure(failure: Failure){
        _products.value = Resource.Error(failure)
    }



    //      ----------------------------------------------------------------------------------------

    private val _editablePosition = MutableLiveData(0)
    val editablePosition : LiveData<Int> = _editablePosition

    fun setEditablePosition(position: Int) {
        if(position == _editablePosition.value){
            return
        }
        _editablePosition.value = position
    }
 */