package com.diegoparra.veggie.products.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.diegoparra.veggie.R
import com.diegoparra.veggie.core.Failure
import com.diegoparra.veggie.core.Resource
import com.diegoparra.veggie.core.ResourceViews
import com.diegoparra.veggie.databinding.FragmentProductsListBinding
import com.diegoparra.veggie.products.domain.entities.MainProdWithQuantity
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
        binding.productsList.setHasFixedSize(true)  //  TODO:   Because this could be somewhat not true if a banner is used
        binding.productsList.adapter = adapter
        subscribeUi()
    }

    private fun subscribeUi(){
        val resourceViews = ResourceViews(
                loadingViews = listOf(binding.progressBar),
                successViews = listOf(binding.productsList),
                failureViews = listOf(binding.errorText)
        )
        viewModel.productsList.observe(viewLifecycleOwner) {
            when(it){
                is Resource.Loading -> {
                    resourceViews.displayViewsForState(ResourceViews.State.LOADING)
                }
                is Resource.Success -> {
                    resourceViews.displayViewsForState(ResourceViews.State.SUCCESS)
                    renderProductsList(it.data)
                }
                is Resource.Error -> {
                    resourceViews.displayViewsForState(ResourceViews.State.ERROR)
                    renderFailure(it.failure)
                }
            }
        }
    }

    private fun renderProductsList(productsList: List<MainProdWithQuantity>) {
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