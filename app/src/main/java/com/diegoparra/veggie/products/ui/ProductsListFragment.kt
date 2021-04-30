package com.diegoparra.veggie.products.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.diegoparra.veggie.databinding.FragmentProductsListBinding
import com.diegoparra.veggie.products.viewmodels.ProductsListViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductsListFragment : Fragment() {

    private var _binding : FragmentProductsListBinding? = null
    private val binding get() = _binding!!
    private val viewModel : ProductsListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProductsListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val adapter = ProductsAdapter()
        binding.productsList.setHasFixedSize(true)  //  TODO:   Becuase this could be somewhat not true if a banner is used
        binding.productsList.adapter = adapter
        subscribeUi(adapter)
    }

    private fun subscribeUi(adapter: ProductsAdapter){
        viewModel.products.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}