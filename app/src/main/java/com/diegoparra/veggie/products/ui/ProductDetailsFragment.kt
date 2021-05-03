package com.diegoparra.veggie.products.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.diegoparra.veggie.core.Failure
import com.diegoparra.veggie.core.Resource
import com.diegoparra.veggie.databinding.FragmentProductDetailsBinding
import com.diegoparra.veggie.products.domain.entities.ProdVariationWithQuantity
import com.diegoparra.veggie.products.viewmodels.ProductDetailsViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint      //  Check, without this annotation app was also working
class ProductDetailsFragment : BottomSheetDialogFragment() {

    private var _binding : FragmentProductDetailsBinding? = null
    private val binding get() = _binding!!
    private val viewModel : ProductDetailsViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentProductDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.title.text = viewModel.name
        viewModel.variationsList.observe(viewLifecycleOwner, ::observeVariationsList)
    }

    private fun observeVariationsList(variations: Resource<List<ProdVariationWithQuantity>>){
        when(variations){
            is Resource.Loading -> renderLoadingVariations()
            is Resource.Success -> renderVariationsList(variations.data)
            is Resource.Error -> renderFailureVariations(variations.failure)
        }
    }

    private fun renderLoadingVariations() {
        //  TODO()
    }

    private fun renderVariationsList(variationsList: List<ProdVariationWithQuantity>) {
        binding.variations.text = variationsList.joinToString(separator = "\n") { variation ->
            if(variation.details == null){
                "${variation.varId} -> ${variation.quantity(null)}"
            }else{
                variation.varId + ": \n" +
                        variation.details.joinToString(separator = "\n") { "\t $it -> ${variation.quantity(it)}" }
            }
        }
    }

    private fun renderFailureVariations(failure: Failure) {
        //  TODO()
    }




    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}