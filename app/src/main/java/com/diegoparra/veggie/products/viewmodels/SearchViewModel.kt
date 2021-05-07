package com.diegoparra.veggie.products.viewmodels

import androidx.lifecycle.*
import com.diegoparra.veggie.core.Either
import com.diegoparra.veggie.core.Failure
import com.diegoparra.veggie.core.Resource
import com.diegoparra.veggie.products.domain.entities.MainProdWithQuantity
import com.diegoparra.veggie.products.domain.usecases.GetMainProductsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
        private val getMainProductsUseCase: GetMainProductsUseCase,
        private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _query = MutableStateFlow(savedStateHandle.get(QUERY_SAVED_STATE_KEY) ?: "")

    val productsList: LiveData<Resource<List<MainProdWithQuantity>>> = _query.flatMapLatest {
        Timber.d("query value collected: $it")
        val prods = getMainProductsUseCase(GetMainProductsUseCase.Params.ForSearch(it))
        prods.map {
            if(it is Either.Right){
                Resource.Success(it.b)
            }else{
                Resource.Error(Failure.SearchFailure.NoSearchResults)
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

}

/*
private val _query = MutableStateFlow(savedStateHandle.get(QUERY_SAVED_STATE_KEY) ?: "")

    val productsList: LiveData<Resource<List<MainProdWithQuantity>>> = _query.flatMapLatest {
        Timber.d("query value collected: $it")
        val prods = getMainProductsUseCase(GetMainProductsUseCase.Params.ForSearch(it))
        prods.map {
            if(it is Either.Right){
                Resource.Success(it.b)
            }else{
                Resource.Error(Failure.SearchFailure.NoSearchResults)
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
        _query.value = query
    }

    fun clearQuery() {
        _query.value = ""
    }


    companion object {
        private const val QUERY_SAVED_STATE_KEY = "query"
    }
 */


/*
private val _query = MutableStateFlow("")

    private val _productsList = MutableLiveData<Resource<List<MainProdWithQuantity>>>()
    val productsList: LiveData<Resource<List<MainProdWithQuantity>>> = _productsList

    init {
        viewModelScope.launch {
            _productsList.value = Resource.Error(Failure.SearchFailure.EmptyQuery)
            _query.collect {
                _productsList.value = Resource.Loading()
                if(it.isEmpty()){
                    _productsList.value = Resource.Error(Failure.SearchFailure.EmptyQuery)
                    return@collect
                }else{
                    getMainProductsUseCase(GetMainProductsUseCase.Params.ForSearch(it))
                            .collect {
                                it.fold(::handleFailure, ::handleProductsList)
                            }
                }
            }
        }
    }

    fun setQuery(query: String) {
        _query.value = query
    }

    private fun handleProductsList(productsList: List<MainProdWithQuantity>){
        if(productsList.isNullOrEmpty()){
            _productsList.value = Resource.Error(Failure.ProductsFailure.ProductsNotFound)
        }else{
            _productsList.value = Resource.Success(productsList)
        }
    }

    private fun handleFailure(failure: Failure){
        _productsList.value = Resource.Error(failure)
    }
 */


/*
private var _query = ""
    private var currentSearchJob : Job? = null

    private val _productsList = MutableLiveData<Resource<List<MainProdWithQuantity>>>()
    val productsList: LiveData<Resource<List<MainProdWithQuantity>>> = _productsList


    fun setQuery(query: String) {
        _query = query
        search()
    }

    fun clearQuery() {
        _query = ""
        search()
    }

    private fun search(){
//        currentSearchJob?.cancel()
        viewModelScope.launch {
            if(_query.isEmpty()) {
                _productsList.value = Resource.Error(Failure.SearchFailure.EmptyQuery)
            }else{
                getMainProductsUseCase(GetMainProductsUseCase.Params.ForSearch(_query))
                        .collect {
                            it.fold(::handleFailure, ::handleProductsList)
                        }
            }
        }
    }


    private fun handleProductsList(productsList: List<MainProdWithQuantity>){
        if(productsList.isNullOrEmpty()){
            _productsList.value = Resource.Error(Failure.ProductsFailure.ProductsNotFound)
        }else{
            _productsList.value = Resource.Success(productsList)
        }
    }

    private fun handleFailure(failure: Failure){
        _productsList.value = Resource.Error(failure)
    }
 */