package com.diegoparra.veggie.products.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProductDetailsViewModel @Inject constructor(
        savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val productId = savedStateHandle.get<String>(PROD_MAIN_ID_SAVED_STATE_KEY)!!
    val publicProductId = productId

    companion object {
        //  VERY IMPORTANT: Must be the same key as the one defined in the nav_main.xml
        const val PROD_MAIN_ID_SAVED_STATE_KEY = "mainId"

        /*  Another different way could be by accessing the args in the fragment.
            With args by navArgs, already have access to the values defined in nav_graph, but, they
            are just in the fragment and not in the viewModel. Example using navArgs in Fragment:
            private val args : ProductDetailsFragmentArgs by navArgs()
            binding.text.text = args.mainId
         */
    }
}