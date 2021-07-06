package com.diegoparra.veggie.products.app.viewmodels

import androidx.lifecycle.*
import com.diegoparra.veggie.core.internet.IsInternetAvailableUseCase
import com.diegoparra.veggie.core.kotlin.Resource
import com.diegoparra.veggie.core.kotlin.toResource
import com.diegoparra.veggie.products.app.usecases.GetMainProductsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val isInternetAvailableUseCase: IsInternetAvailableUseCase,
    private val getMainProductsUseCase: GetMainProductsUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _query = savedStateHandle.getLiveData(QUERY_SAVED_STATE_KEY, "").asFlow()

    fun setQuery(query: String) {
        Timber.d("setQuery() called with: query = $query")
        savedStateHandle.set(QUERY_SAVED_STATE_KEY, query)
    }

    fun clearQuery() {
        savedStateHandle.set(QUERY_SAVED_STATE_KEY, "")
    }

    //      ---------------------------------------------------------------

    private val _isInternetAvailable = isInternetAvailableUseCase()

    private val _paramsPair =
        combine(_query, _isInternetAvailable) { query, internetAvailable ->
            Pair(first = query, second = internetAvailable)
        }


    //      ---------------------------------------------------------------

    //  Internet access is not really necessary, but should be included in combine flows, so that
    //  list is reloaded on any internetAccess change.
    val productsList = _paramsPair
        .flatMapLatest { (query, _) ->
            getMainProductsUseCase(GetMainProductsUseCase.Params.ForSearch(query))
                .map { it.toResource() }
                .onStart { emit(Resource.Loading()) }
        }
        .asLiveData()


    //      ---------------------------------------------------------------

    companion object {
        private const val QUERY_SAVED_STATE_KEY = "query"
    }

}