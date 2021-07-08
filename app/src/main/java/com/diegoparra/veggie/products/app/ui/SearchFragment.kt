package com.diegoparra.veggie.products.app.ui

import android.os.Bundle
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.diegoparra.veggie.R
import com.diegoparra.veggie.core.kotlin.Failure
import com.diegoparra.veggie.core.kotlin.Resource
import com.diegoparra.veggie.core.android.addTextChangedListenerDistinctChanged
import com.diegoparra.veggie.core.android.hideKeyboard
import com.diegoparra.veggie.databinding.FragmentSearchBinding
import com.diegoparra.veggie.products.app.entities.ProductMain
import com.diegoparra.veggie.products.app.viewmodels.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SearchViewModel by viewModels()
    private val adapter by lazy { ProductsAdapter() }
    private var searchTextWatcher: TextWatcher? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        searchBarFunctionality()
        searchResultsList()
    }


    //      ----------------------------------------------------------------------------------------

    private fun searchBarFunctionality() {
        binding.clearSearchText.visibility = View.GONE
        binding.clearSearchText.setOnClickListener {
            viewModel.clearQuery()
            binding.searchQuery.setText("")
        }
        searchTextWatcher = binding.searchQuery.addTextChangedListenerDistinctChanged {
            viewModel.setQuery(it.toString())
            val btnClearVisibility = it.toString().isNotEmpty()
            if (binding.clearSearchText.isVisible != btnClearVisibility) {
                binding.clearSearchText.isVisible = btnClearVisibility
            }
        }
        binding.searchQuery.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                v.hideKeyboard()
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }
    }


    //      ----------------------------------------------------------------------------------------

    private fun searchResultsList() {
        binding.searchResults.setHasFixedSize(true)
        binding.searchResults.adapter = adapter
        subscribeUiResults()
    }

    private fun subscribeUiResults() {
        viewModel.productsList.observe(viewLifecycleOwner) {
            //  Set views visibility based on Resource state
            binding.progressBar.isVisible = it is Resource.Loading
            binding.searchResults.isVisible = it is Resource.Success
            binding.layoutNoSearchResults.isVisible = false     //  Will be set to tru when necessary inside renderProductsList
            binding.errorText.isVisible = it is Resource.Error

            when (it) {
                is Resource.Loading -> {}
                is Resource.Success -> renderProductsList(it.data)
                is Resource.Error -> renderFailure(it.failure)
            }
        }
    }

    private fun renderProductsList(productsList: List<ProductMain>) {
        //  Should be always called. If the list is empty, it will be shown as blank
        adapter.submitList(productsList)

        if (productsList.isEmpty()) {
            Timber.d("Products list search results is empty")
            binding.textNoSearchResults.text = if (binding.searchQuery.text.isNullOrEmpty()) {
                getString(R.string.empty_search_query)
            } else {
                getString(R.string.no_search_results)
            }
            binding.layoutNoSearchResults.isVisible = true
        }
    }

    private fun renderFailure(failure: Failure) {
        binding.errorText.text = failure.toString()
    }


    //      ----------------------------------------------------------------------------------------

    override fun onDestroyView() {
        super.onDestroyView()
        searchTextWatcher?.let { binding.searchQuery.removeTextChangedListener(it) }
        _binding = null
    }

}