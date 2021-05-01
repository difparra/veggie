package com.diegoparra.veggie.products.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.diegoparra.veggie.core.Failure
import com.diegoparra.veggie.databinding.FragmentProductsListBinding
import com.diegoparra.veggie.products.domain.entities.MainProdWithQuantity
import com.diegoparra.veggie.products.viewmodels.ProductsListState
import com.diegoparra.veggie.products.viewmodels.ProductsListViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductsListFragment : Fragment() {

    private var _binding : FragmentProductsListBinding? = null
    private val binding get() = _binding!!
    private val viewModel : ProductsListViewModel by viewModels()
    private val adapter = ProductsAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProductsListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.productsList.setHasFixedSize(true)  //  TODO:   Becuase this could be somewhat not true if a banner is used
        binding.productsList.adapter = adapter
        subscribeUi()
    }

    private fun subscribeUi(){
        viewModel.productsListState.observe(viewLifecycleOwner) {
            when(it){
                is ProductsListState.Loading -> renderLoadingState()
                is ProductsListState.Success -> renderProductsList(it.data)
                is ProductsListState.EmptyProductsList -> renderEmptyListState()
                is ProductsListState.UnknownError -> renderFailure(it.failure, it.message)
            }
        }
    }

    private fun renderLoadingState() {
        //  TODO()
    }

    private fun renderProductsList(productsList: List<MainProdWithQuantity>) {
        adapter.submitList(productsList)
    }

    private fun renderEmptyListState() {
        TODO()
    }

    private fun renderFailure(failure: Failure, message: String?) {
        TODO()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}