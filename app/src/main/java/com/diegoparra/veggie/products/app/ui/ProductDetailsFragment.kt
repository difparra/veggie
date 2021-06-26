package com.diegoparra.veggie.products.app.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.diegoparra.veggie.R
import com.diegoparra.veggie.core.kotlin.Failure
import com.diegoparra.veggie.core.kotlin.Resource
import com.diegoparra.veggie.databinding.FragmentProductDetailsBinding
import com.diegoparra.veggie.products.app.entities.ProductVariation
import com.diegoparra.veggie.products.app.viewmodels.ProductDetailsViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class ProductDetailsFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentProductDetailsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProductDetailsViewModel by viewModels()
    private val adapter by lazy {
        VariationsAdapter(object : VariationsAdapter.OnItemClickListener {
            override fun onItemClick(variationId: String, detail: String?, which: Int) {
                when (which) {
                    VariationsAdapter.OnItemClickListener.BUTTON_ADD -> viewModel.addQuantity(
                        varId = variationId,
                        detail = detail
                    )
                    VariationsAdapter.OnItemClickListener.BUTTON_REDUCE -> viewModel.reduceQuantity(
                        varId = variationId,
                        detail = detail
                    )
                }
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProductDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.title.text = viewModel.name
        //  can't use setHasFixedSize(true) as BottomSheetDialog depends on its size. it set true, recyclerView will not show
        binding.variationsList.adapter = adapter
        subscribeUi()
    }

    private fun subscribeUi() {
        viewModel.variationsList.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading ->
                    setViewsVisibility(loadingViews = true, successViews = false, errorViews = false)
                is Resource.Success -> {
                    setViewsVisibility(loadingViews = false, successViews = true, errorViews = false)
                    renderVariationsList(it.data)
                }
                is Resource.Error -> {
                    setViewsVisibility(loadingViews = false, successViews = false, errorViews = true)
                    renderFailureVariations(it.failure)
                }
            }
        }
    }

    private fun setViewsVisibility(loadingViews: Boolean, successViews: Boolean, errorViews: Boolean) {
        binding.progressBar.isVisible = loadingViews
        binding.variationsList.isVisible = successViews
        binding.errorText.isVisible = errorViews
    }

    private fun renderVariationsList(variationsList: List<ProductVariation>) {
        //  Dealing with empty list
        if (variationsList.isEmpty()) {
            Timber.wtf("Got empty list in variations after selecting a main product.")
            binding.errorText.text = getString(R.string.failure_no_variations_found)
            binding.errorText.isVisible = true
        }

        val listToSubmit = VariationUi.getListToSubmit(variationsList)
        adapter.submitList(listToSubmit)
    }

    private fun renderFailureVariations(failure: Failure) {
        binding.errorText.text = failure.toString()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}