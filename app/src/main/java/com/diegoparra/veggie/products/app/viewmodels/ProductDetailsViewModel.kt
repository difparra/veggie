package com.diegoparra.veggie.products.app.viewmodels

import androidx.lifecycle.*
import com.diegoparra.veggie.core.Failure
import com.diegoparra.veggie.core.Resource
import com.diegoparra.veggie.products.cart.domain.ProductId
import com.diegoparra.veggie.products.app.entities.ProductVariation
import com.diegoparra.veggie.products.app.usecases.GetVariationsUseCase
import com.diegoparra.veggie.products.app.usecases.UpdateQuantityUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ProductDetailsViewModel @Inject constructor(
    private val getVariationsUseCase: GetVariationsUseCase,
    private val updateQuantityUseCase: UpdateQuantityUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val mainId = savedStateHandle.get<String>(PROD_MAIN_ID_SAVED_STATE_KEY)!!
    val name = savedStateHandle.get<String>(PROD_MAIN_NAME_SAVED_STATE_KEY)!!

    private val _variationsList = MutableLiveData<Resource<List<ProductVariation>>>()
    val variationsList: LiveData<Resource<List<ProductVariation>>> = _variationsList

    init {
        viewModelScope.launch {
            _variationsList.value = Resource.Loading()
            getVariationsUseCase(mainId).collect {
                Timber.d("variations collected: $it")
                it.fold(::handleFailure, ::handleVariationsList)
            }
        }
    }

    private fun handleVariationsList(variations: List<ProductVariation>) {
        if (variations.isNullOrEmpty()) {
            _variationsList.value = Resource.Error(Failure.ProductsFailure.ProductsNotFound)
        } else {
            _variationsList.value = Resource.Success(variations)
        }
    }

    private fun handleFailure(failure: Failure) {
        _variationsList.value = Resource.Error(failure)
    }


    //      ----------------------------------------------------------------------------------------

    fun addQuantity(varId: String, detail: String?) {
        Timber.d("addQuantity() called with: varId = $varId, detail = $detail")
        val prodVariation = findVariation(varId)
        prodVariation?.let { variation ->
            viewModelScope.launch {
                val productId = ProductId(mainId = mainId, varId = varId, detail = detail)
                val maxOrder = variation.maxOrder
                updateQuantityUseCase(UpdateQuantityUseCase.Params.Add(productId, maxOrder))
            }
        }
        if (prodVariation == null) {
            Timber.e("Variation $varId was not found in ProductDetailsViewModel products.")
        }
    }

    fun reduceQuantity(varId: String, detail: String?) {
        Timber.d("reduceQuantity() called with: varId = $varId, detail = $detail")
        //  Not strictly necessary to findVariation, but better as it is a validation if variation
        //  exists in the products send to show in ui
        val prodVariation = findVariation(varId = varId)
        prodVariation?.let {
            viewModelScope.launch {
                val productId = ProductId(mainId = mainId, varId = varId, detail = detail)
                updateQuantityUseCase(UpdateQuantityUseCase.Params.Reduce(productId))
            }
        }
        if (prodVariation == null) {
            Timber.e("Variation $varId was not found in ProductDetailsViewModel products.")
        }
    }

    private fun findVariation(varId: String): ProductVariation? {
        return when (val variations = variationsList.value) {
            is Resource.Success -> variations.data.find { it.varId == varId }
            else -> null
        }
    }


    //      ----------------------------------------------------------------------------------------

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