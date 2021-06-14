package com.diegoparra.veggie.products.app.viewmodels

import androidx.lifecycle.*
import com.diegoparra.veggie.core.Failure
import com.diegoparra.veggie.core.Resource
import com.diegoparra.veggie.products.app.entities.ProductMain
import com.diegoparra.veggie.products.app.usecases.GetMainProductsUseCase
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

    /*
        NOTE:   https://www.youtube.com/watch?v=B8ppnjGPAGE
                liveDataCoroutineBuilder can assign Dispatchers to LiveData,
                so I can switchMap on another dispatcher and use LiveData instead of StateFlow
                liveData(Dispatchers.IO) { emit(__) }
                The problem with liveData is that map run on MainThread, so it is better to use switchMap
                and return a liveData created with a builder.
                Flow map is in the coroutine context so function can be normally called and then call asLiveData.
     */

    private val _query = MutableStateFlow(savedStateHandle.get(QUERY_SAVED_STATE_KEY) ?: "")
    private var currentJobSearch: Job? = null

    private val _productsList =
        MutableLiveData<Resource<List<ProductMain>>>(Resource.Error(Failure.SearchFailure.EmptyQuery))
    val productsList: LiveData<Resource<List<ProductMain>>> = _productsList

    init {
        viewModelScope.launch {
            _query.collect {
                savedStateHandle.set(QUERY_SAVED_STATE_KEY, it)
                currentJobSearch?.cancel()

                _productsList.value = Resource.Loading()
                val prods = getMainProductsUseCase(GetMainProductsUseCase.Params.ForSearch(it))

                //  As in cartViewModel, this new job should be launched in a different viewModelScope,
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
        _productsList.value = Resource.Success(products)
    }

    private fun handleFailure(failure: Failure) {
        _productsList.value = Resource.Error(failure)
    }

    companion object {
        private const val QUERY_SAVED_STATE_KEY = "query"
    }

}