package com.diegoparra.veggie.products.viewmodels

import androidx.lifecycle.*
import com.diegoparra.veggie.core.Failure
import com.diegoparra.veggie.core.Resource
import com.diegoparra.veggie.products.domain.entities.ProductMain
import com.diegoparra.veggie.products.domain.usecases.GetMainProductsUseCase
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
    private var currentJobSearch : Job? = null

    private val _productsList = MutableLiveData<Resource<List<ProductMain>>>(Resource.Error(Failure.SearchFailure.EmptyQuery))
    val productsList : LiveData<Resource<List<ProductMain>>> = _productsList

    init {
        viewModelScope.launch {
            _query.collect {
                savedStateHandle.set(QUERY_SAVED_STATE_KEY, it)
                currentJobSearch?.cancel()

                _productsList.value = Resource.Loading()
                val prods = getMainProductsUseCase(GetMainProductsUseCase.Params.ForSearch(it))
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

    private fun handleFailure(failure: Failure){
        _productsList.value = Resource.Error(failure)
    }

    companion object {
        private const val QUERY_SAVED_STATE_KEY = "query"
    }

}



/*

//  WORKING, BUT LOADING STATE COULD NOT REALLY BE MANAGED PROPERLY

    private val _query = MutableStateFlow(savedStateHandle.get(QUERY_SAVED_STATE_KEY) ?: "")

    val productsList: LiveData<Resource<List<MainProdWithQuantity>>> = _query.flatMapLatest {
        Timber.d("query value collected: $it")
        val prods = getMainProductsUseCase(GetMainProductsUseCase.Params.ForSearch(it))
        prods.map {
            if(it is Either.Right){
                Resource.Success(it.b)
            }else{
                Resource.Error((it as Either.Left).a)
            }
        }
    }.asLiveData()

    init {
        viewModelScope.launch {
            _query.collect {
                savedStateHandle.set(QUERY_SAVED_STATE_KEY, it)
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


    companion object {
        private const val QUERY_SAVED_STATE_KEY = "query"
    }
 */