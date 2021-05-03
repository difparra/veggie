package com.diegoparra.veggie.products.viewmodels

import androidx.lifecycle.*
import com.diegoparra.veggie.core.Failure
import com.diegoparra.veggie.core.Resource
import com.diegoparra.veggie.products.domain.entities.ProdVariationWithQuantity
import com.diegoparra.veggie.products.domain.usecases.GetVariationsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailsViewModel @Inject constructor(
        private val getVariationsUseCase: GetVariationsUseCase,
        savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val productId = savedStateHandle.get<String>(PROD_MAIN_ID_SAVED_STATE_KEY)!!
    val name = savedStateHandle.get<String>(PROD_MAIN_NAME_SAVED_STATE_KEY)!!

    private val _variationsList = MutableLiveData<Resource<List<ProdVariationWithQuantity>>>()
    val variationsList : LiveData<Resource<List<ProdVariationWithQuantity>>> = _variationsList

    init {
        viewModelScope.launch {
            _variationsList.value = Resource.Loading()
            getVariationsUseCase(productId).collect {
                it.fold(::handleFailure, ::handleVariationsList)
            }
        }
    }

    private fun handleVariationsList(variations: List<ProdVariationWithQuantity>) {
        _variationsList.value = Resource.Success(variations)
    }

    private fun handleFailure(failure: Failure) {
        _variationsList.value = Resource.Error(failure)
    }


    companion object {
        //  VERY IMPORTANT: Must be the same key as the one defined in the nav_main.xml
        const val PROD_MAIN_ID_SAVED_STATE_KEY = "mainId"
        const val PROD_MAIN_NAME_SAVED_STATE_KEY = "name"

        /*  Another different way could be by accessing the args in the fragment.
            With args by navArgs, already have access to the values defined in nav_graph, but, they
            are just in the fragment and not in the viewModel. Example using navArgs in Fragment:
            private val args : ProductDetailsFragmentArgs by navArgs()
            binding.text.text = args.mainId
         */
    }
}