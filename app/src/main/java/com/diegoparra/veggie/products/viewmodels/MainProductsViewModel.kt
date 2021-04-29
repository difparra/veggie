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
class MainProductsViewModel @Inject constructor(
    private val getMainProductsUseCase: GetMainProductsUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val tagId = savedStateHandle.get<String>(TAG_ID_SAVED_STATE_KEY)!!  //  Must be set when instantiating the fragment in the tabs adapter.

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private val _failure = MutableLiveData<Failure>()
    val failure: LiveData<Failure> = _failure

    private val _products = MutableLiveData<List<MainProdWithQuantity>>(listOf())
    val products: LiveData<List<MainProdWithQuantity>> = _products

    init {
        viewModelScope.launch {
            _loading.value = true
            getMainProductsUseCase(GetMainProductsUseCase.Params.ForTag(tagId))
                .collect { prodsEither ->
                    prodsEither.fold({
                        _failure.value = it
                        true
                    }, {
                        _products.value = it
                        true
                    })
                }
            _loading.value = false
        }
    }

    companion object {
        const val TAG_ID_SAVED_STATE_KEY = "tagId"
    }

}