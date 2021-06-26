package com.diegoparra.veggie.products.app.viewmodels

import androidx.lifecycle.*
import com.diegoparra.veggie.core.kotlin.Failure
import com.diegoparra.veggie.core.kotlin.Resource
import com.diegoparra.veggie.products.app.entities.ProductMain
import com.diegoparra.veggie.products.app.usecases.GetMainProductsUseCase
import com.diegoparra.veggie.products.utils.ProductsFailure
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val getMainProductsUseCase: GetMainProductsUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _query = MutableStateFlow(savedStateHandle.get(QUERY_SAVED_STATE_KEY) ?: "")
    private var currentJobSearch: Job? = null

    private val _productsList =
        MutableLiveData<Resource<List<ProductMain>>>(Resource.Error(ProductsFailure.EmptySearchQuery))
    val productsList: LiveData<Resource<List<ProductMain>>> = _productsList

    init {
        viewModelScope.launch {
            _query.collect {
                savedStateHandle.set(QUERY_SAVED_STATE_KEY, it)
                currentJobSearch?.cancel()

                Timber.d("query collected: $it, calling loading")
                _productsList.value = Resource.Loading()
                val prods = getMainProductsUseCase(GetMainProductsUseCase.Params.ForSearch(it))
                Timber.d("useCase called!")
                //  This new job should be launched in a different viewModelScope,
                //  otherwise, values will not be collected.
                currentJobSearch = prods
                    .onEach {
                        it.fold(::handleFailure, ::handleProducts)
                    }
                    .launchIn(viewModelScope)
            }
        }
    }

    fun setQuery(query: String) {
        Timber.d("setQuery() called with: query = $query")
        _query.value = query
    }

    fun clearQuery() {
        _query.value = ""
    }


    private fun handleProducts(products: List<ProductMain>) {
        Timber.d("handleProducts called with products = $products")
        _productsList.value = Resource.Success(products)
    }

    private fun handleFailure(failure: Failure) {
        Timber.d("handleFailure called with failure = $failure")
        _productsList.value = Resource.Error(failure)
    }

    companion object {
        private const val QUERY_SAVED_STATE_KEY = "query"
    }

}