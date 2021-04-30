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

    private val _status = MutableLiveData<ProductsListStatus>()
    val status : LiveData<ProductsListStatus> = _status

    private val _products = MutableLiveData<List<MainProdWithQuantity>>(listOf())
    val products: LiveData<List<MainProdWithQuantity>> = _products

    private val _failure = MutableLiveData<Failure>()
    val failure: LiveData<Failure> = _failure


    init {
        viewModelScope.launch {
            _status.value = ProductsListStatus.LOADING
            getMainProductsUseCase(GetMainProductsUseCase.Params.ForTag(tagId))
                .collect { prodsEither ->
                    if(_status.value != ProductsListStatus.LOADING){
                        _status.value = ProductsListStatus.LOADING
                    }
                    prodsEither.fold({
                        _status.value = ProductsListStatus.ERROR
                        _failure.value = it
                        true
                    }, {
                        _products.value = it
                        _status.value = ProductsListStatus.DONE
                        true
                    })
                }
        }
    }

    companion object {
        const val TAG_ID_SAVED_STATE_KEY = "tagId"
    }

}


enum class ProductsListStatus { LOADING, ERROR, DONE }