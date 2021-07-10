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
class ProductsListViewModel @Inject constructor(
    private val isInternetAvailableUseCase: IsInternetAvailableUseCase,
    private val getMainProductsUseCase: GetMainProductsUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val tagId =
        savedStateHandle.get<String>(TAG_ID_SAVED_STATE_KEY)!!  //  Must be set when instantiating the fragment in the tabs adapter.

    private val _isInternetAvailable = isInternetAvailableUseCase()

    //  Combine with isInternetAvailable so that list will be reloaded on internetAccess change.
    val productsList = _isInternetAvailable
        .flatMapLatest {
            Timber.d("isInternetAvailable = $it, Calling getMainProductsUseCase")
            getMainProductsUseCase(GetMainProductsUseCase.Params.ForTag(tagId))
                .map { it.toResource() }
                .onStart { emit(Resource.Loading) }
        }
        .asLiveData()


    companion object {
        const val TAG_ID_SAVED_STATE_KEY = "tagId"
    }

}