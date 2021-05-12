package com.diegoparra.veggie.products.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.diegoparra.veggie.R
import com.diegoparra.veggie.core.Failure
import com.diegoparra.veggie.core.Resource
import com.diegoparra.veggie.databinding.FragmentProductDetailsBinding
import com.diegoparra.veggie.products.domain.entities.ProductVariation
import com.diegoparra.veggie.products.viewmodels.ProductDetailsViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint      //  Check, without this annotation app was also working
class ProductDetailsFragment : BottomSheetDialogFragment(), VariationsAdapter.OnItemClickListener {

    private var _binding : FragmentProductDetailsBinding? = null
    private val binding get() = _binding!!
    private val viewModel : ProductDetailsViewModel by viewModels()
    private val adapter by lazy { VariationsAdapter(this) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentProductDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.title.text = viewModel.name
        //  can't use setHasFixedSize(true) as BottomSheetDialog depends on its size. it set true, recyclerView will not show
        binding.variationsList.adapter = adapter
        subscribeUi()
    }

    private fun subscribeUi(){
        viewModel.variationsList.observe(viewLifecycleOwner) {
            when(it){
                is Resource.Loading -> {
                    binding.progressBar.isVisible = true
                    binding.variationsList.isVisible = false
                    binding.errorText.isVisible = false
                }
                is Resource.Success -> {
                    binding.progressBar.isVisible = false
                    binding.variationsList.isVisible = true
                    binding.errorText.isVisible = false
                    renderVariationsList(it.data)
                }
                is Resource.Error -> {
                    binding.progressBar.isVisible = false
                    binding.variationsList.isVisible = false
                    binding.errorText.isVisible = true
                    renderFailureVariations(it.failure)
                }
            }
        }
    }

    private fun renderVariationsList(variationsList: List<ProductVariation>) {
        val listToSubmit = VariationUi.getListToSubmit(variationsList)
        Timber.d(listToSubmit.toString())
        adapter.submitList(listToSubmit)
    }

    override fun onItemClick(variationId: String, detail: String?, which: Int) {
        when(which){
            VariationsAdapter.OnItemClickListener.BUTTON_ADD -> viewModel.addQuantity(varId = variationId, detail = detail)
            VariationsAdapter.OnItemClickListener.BUTTON_REDUCE -> viewModel.reduceQuantity(varId = variationId, detail = detail)
        }
    }

    private fun renderFailureVariations(failure: Failure) {
        when(failure){
            is Failure.ProductsFailure.ProductsNotFound ->
                binding.errorText.text = getString(R.string.failure_generic)
            else ->
                binding.errorText.text = failure.toString()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}