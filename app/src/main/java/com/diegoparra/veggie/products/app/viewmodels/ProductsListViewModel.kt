package com.diegoparra.veggie.products.app.viewmodels

import androidx.lifecycle.*
import com.diegoparra.veggie.core.kotlin.Failure
import com.diegoparra.veggie.core.kotlin.Resource
import com.diegoparra.veggie.products.app.entities.ProductMain
import com.diegoparra.veggie.products.app.usecases.GetMainProductsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductsListViewModel @Inject constructor(
    private val getMainProductsUseCase: GetMainProductsUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val tagId =
        savedStateHandle.get<String>(TAG_ID_SAVED_STATE_KEY)!!  //  Must be set when instantiating the fragment in the tabs adapter.

    private val _productsList = MutableLiveData<Resource<List<ProductMain>>>()
    val productsList: LiveData<Resource<List<ProductMain>>> = _productsList

    init {
        viewModelScope.launch {
            _productsList.value = Resource.Loading()
            getMainProductsUseCase(GetMainProductsUseCase.Params.ForTag(tagId))
                .collect { prodsEither ->
                    //  Do not set loading here, as it would hide the list on every change like
                    //  quantity. Normally, this changes will be fast.
                    prodsEither.fold(::handleFailure, ::handleProductsList)
                }
        }
    }

    private fun handleProductsList(productsList: List<ProductMain>) {
        if (productsList.isNullOrEmpty()) {
            _productsList.value = Resource.Error(Failure.ProductsFailure.ProductsNotFound)
        } else {
            _productsList.value = Resource.Success(productsList)
        }
    }

    private fun handleFailure(failure: Failure) {
        _productsList.value = Resource.Error(failure)
    }

    companion object {
        const val TAG_ID_SAVED_STATE_KEY = "tagId"
    }

}