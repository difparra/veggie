package com.diegoparra.veggie.products.viewmodels

import androidx.lifecycle.*
import com.diegoparra.veggie.core.Failure
import com.diegoparra.veggie.products.domain.entities.MainProdWithQuantity
import com.diegoparra.veggie.products.domain.usecases.GetMainProductsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductsListViewModel @Inject constructor(
    private val getMainProductsUseCase: GetMainProductsUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val tagId = savedStateHandle.get<String>(TAG_ID_SAVED_STATE_KEY)!!  //  Must be set when instantiating the fragment in the tabs adapter.

    private val _productsListState = MutableLiveData<ProductsListState>()
    val productsListState: LiveData<ProductsListState> = _productsListState

    init {
        viewModelScope.launch {
            _productsListState.value = ProductsListState.Loading
            getMainProductsUseCase(GetMainProductsUseCase.Params.ForTag(tagId))
                .collect { prodsEither ->
                    //_productsListState.value = ProductsListState.Loading
                    prodsEither.fold(::handleFailure, ::handleProductsList)
                }
        }
    }

    private fun handleProductsList(productsList: List<MainProdWithQuantity>){
        if(productsList.isNullOrEmpty()){
            _productsListState.value = ProductsListState.EmptyProductsList
        }else{
            _productsListState.value = ProductsListState.Success(productsList)
        }
    }

    private fun handleFailure(failure: Failure){
        _productsListState.value = when(failure){
            is Failure.ProductsFailure.ProductsNotFound ->
                ProductsListState.EmptyProductsList
            else ->
                ProductsListState.UnknownError(failure, failure.toString())
        }
    }

    companion object {
        const val TAG_ID_SAVED_STATE_KEY = "tagId"
    }

}

sealed class ProductsListState {
    object Loading : ProductsListState()
    class Success(val data: List<MainProdWithQuantity>) : ProductsListState()
    object EmptyProductsList : ProductsListState()
    class UnknownError(val failure: Failure, val message: String? = null) : ProductsListState()
}