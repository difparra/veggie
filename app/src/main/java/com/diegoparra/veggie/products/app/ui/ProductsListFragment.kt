package com.diegoparra.veggie.products.app.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.diegoparra.veggie.R
import com.diegoparra.veggie.core.kotlin.Failure
import com.diegoparra.veggie.core.kotlin.Resource
import com.diegoparra.veggie.databinding.FragmentProductsListBinding
import com.diegoparra.veggie.products.app.entities.ProductMain
import com.diegoparra.veggie.products.app.viewmodels.ProductsListViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class ProductsListFragment : Fragment() {

    private var _binding: FragmentProductsListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProductsListViewModel by viewModels()
    private val adapter by lazy { ProductsAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProductsListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //  Test setHasFixedSize when banner is added.
        //  App could crash or products list could be not visible as it happened in productVariations.
        binding.productsList.setHasFixedSize(true)
        binding.productsList.adapter = adapter
        subscribeUi()
    }

    private fun subscribeUi() {
        viewModel.productsList.observe(viewLifecycleOwner) {
            Timber.d("productsList collected = $it")
            when (it) {
                is Resource.Loading ->
                    setViewsVisibility(loadingViews = true, mainViews = false, errorViews = false)
                is Resource.Success -> {
                    setViewsVisibility(loadingViews = false, mainViews = true, errorViews = false)
                    renderProductsList(it.data)
                }
                is Resource.Error -> {
                    setViewsVisibility(loadingViews = false, mainViews = false, errorViews = true)
                    renderFailure(it.failure)
                }
            }
        }
    }

    private fun setViewsVisibility(loadingViews: Boolean, mainViews: Boolean, errorViews: Boolean) {
        binding.progressBar.isVisible = loadingViews
        binding.productsList.isVisible = mainViews
        binding.errorText.isVisible = errorViews
    }

    private fun renderProductsList(productsList: List<ProductMain>) {
        //  Dealing with empty list
        if (productsList.isEmpty()) {
            Timber.w("Should not be empty list for a tag. It could be an admin problem by setting either the id of the tag, or the products in the tag")
            binding.errorText.text = getString(R.string.failure_no_products_for_tag)
            binding.errorText.isVisible = true
        }

        adapter.submitList(productsList)
    }

    private fun renderFailure(failure: Failure) {
        binding.errorText.text = failure.toString()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}