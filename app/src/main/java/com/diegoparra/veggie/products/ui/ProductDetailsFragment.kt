package com.diegoparra.veggie.products.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.diegoparra.veggie.core.Failure
import com.diegoparra.veggie.core.Resource
import com.diegoparra.veggie.databinding.FragmentProductDetailsBinding
import com.diegoparra.veggie.products.domain.entities.ProdVariationWithQuantities
import com.diegoparra.veggie.products.viewmodels.ProductDetailsViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint      //  Check, without this annotation app was also working
class ProductDetailsFragment : BottomSheetDialogFragment() {

    private var _binding : FragmentProductDetailsBinding? = null
    private val binding get() = _binding!!
    private val viewModel : ProductDetailsViewModel by viewModels()
    private val adapter by lazy { VariationsAdapter() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentProductDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.title.text = viewModel.name
        //  can't use setHasFixedSize(true) as this fragment is a bottomDialog and will
        //  extend as long as the recyclerview. RecyclerView has no fixed size, it set so, nothing
        //  will be shown.
        binding.variationsList.adapter = adapter
        viewModel.variationsList.observe(viewLifecycleOwner, ::observeVariationsList)
    }

    private fun observeVariationsList(variations: Resource<List<ProdVariationWithQuantities>>){
        when(variations){
            is Resource.Loading -> renderLoadingVariations()
            is Resource.Success -> renderVariationsList(variations.data)
            is Resource.Error -> renderFailureVariations(variations.failure)
        }
    }

    private fun renderLoadingVariations() {
        //  TODO()
    }

    private fun renderVariationsList(variationsList: List<ProdVariationWithQuantities>) {
        val listToSubmit = VariationUi.getListToSubmit(variationsList)
        Timber.d(listToSubmit.toString())
        adapter.submitList(listToSubmit)
    }

    private fun renderFailureVariations(failure: Failure) {
        //  TODO()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}