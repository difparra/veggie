package com.diegoparra.veggie.products.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.diegoparra.veggie.R
import com.diegoparra.veggie.core.Failure
import com.diegoparra.veggie.core.Resource
import com.diegoparra.veggie.databinding.FragmentProductsListBinding
import com.diegoparra.veggie.products.entities.ProductMain
import com.diegoparra.veggie.products.viewmodels.ProductsListViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductsListFragment : Fragment() {

    private var _binding : FragmentProductsListBinding? = null
    private val binding get() = _binding!!
    private val viewModel : ProductsListViewModel by viewModels()
    private val adapter by lazy { ProductsAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProductsListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.productsList.setHasFixedSize(true)
            //  TODO:   Check app do not crash and products list is still visible when a banner is
            //          added. setHasFixedSize didn't work in productVariations.
        binding.productsList.adapter = adapter
        subscribeUi()
    }

    private fun subscribeUi(){
        viewModel.productsList.observe(viewLifecycleOwner) {
            when(it){
                is Resource.Loading -> {
                    binding.progressBar.isVisible = true
                    binding.productsList.isVisible = false
                    binding.errorText.isVisible = false
                }
                is Resource.Success -> {
                    binding.progressBar.isVisible = false
                    binding.productsList.isVisible = true
                    binding.errorText.isVisible = false
                    renderProductsList(it.data)
                }
                is Resource.Error -> {
                    binding.progressBar.isVisible = false
                    binding.productsList.isVisible = false
                    binding.errorText.isVisible = true
                    renderFailure(it.failure)
                }
            }
        }
    }

    private fun renderProductsList(productsList: List<ProductMain>) {
        adapter.submitList(productsList)
    }

    private fun renderFailure(failure: Failure) {
        when(failure){
            is Failure.ProductsFailure.ProductsNotFound ->
                binding.errorText.text = getString(R.string.failure_no_products_for_tag)
            else ->
                binding.errorText.text = failure.toString()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}