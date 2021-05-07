package com.diegoparra.veggie.products.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.diegoparra.veggie.core.Failure
import com.diegoparra.veggie.core.Resource
import com.diegoparra.veggie.databinding.FragmentSearchBinding
import com.diegoparra.veggie.products.domain.entities.MainProdWithQuantity
import com.diegoparra.veggie.products.viewmodels.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private var _binding : FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val viewModel : SearchViewModel by viewModels()
    private val adapter by lazy { ProductsAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.searchResults.setHasFixedSize(true)
        binding.searchResults.adapter = adapter
        searchFunctionality()
        subscribeUi()
    }


    private fun searchFunctionality(){
        binding.searchQuery.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                viewModel.setQuery(s.toString())
            }
        })
        binding.clearSearchText.setOnClickListener {
            viewModel.clearQuery()
        }
    }



    private fun subscribeUi() {
        viewModel.productsList.observe(viewLifecycleOwner) {
            when(it){
                is Resource.Loading -> renderLoadingState()
                is Resource.Success -> renderProductsList(it.data)
                is Resource.Error -> renderFailure(it.failure)
            }
        }
    }

    private fun renderLoadingState() {
        //  TODO()
    }

    private fun renderProductsList(productsList: List<MainProdWithQuantity>) {
        adapter.submitList(productsList)
    }

    private fun renderFailure(failure: Failure) {
        Toast.makeText(binding.root.context, "Failure: $failure", Toast.LENGTH_SHORT).show()
        /*when(failure){
            is Failure.ProductsFailure.ProductsNotFound ->
                renderEmptyListFailure()
            else ->
                TODO()
        }*/
    }

    private fun renderEmptyListFailure() {
        TODO()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}