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
import com.diegoparra.veggie.core.Failure
import com.diegoparra.veggie.core.Resource
import com.diegoparra.veggie.core.addTextChangedListenerDistinctChanged
import com.diegoparra.veggie.databinding.FragmentSearchBinding
import com.diegoparra.veggie.products.app.entities.ProductMain
import com.diegoparra.veggie.core.hideKeyboard
import com.diegoparra.veggie.products.app.viewmodels.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint

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
            when (it) {
                is Resource.Loading -> {
                    binding.progressBar.isVisible = true
                    binding.searchResults.isVisible = false
                    binding.errorText.isVisible = false
                }
                is Resource.Success -> {
                    binding.progressBar.isVisible = false
                    binding.searchResults.isVisible = true
                    binding.errorText.isVisible = false
                    renderProductsList(it.data)
                }
                is Resource.Error -> {
                    binding.progressBar.isVisible = false
                    binding.searchResults.isVisible = false
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
        when (failure) {
            is Failure.SearchFailure.EmptyQuery -> {
                cleanRecyclerView()
                binding.errorText.text = getString(R.string.failure_empty_search_query)
            }
            is Failure.SearchFailure.NoSearchResults -> {
                cleanRecyclerView()
                binding.errorText.text = getString(R.string.failure_no_search_result)
            }
            else -> {
                cleanRecyclerView()
                binding.errorText.text = failure.toString()
            }
        }
    }

    private fun cleanRecyclerView() {
        adapter.submitList(listOf())
        binding.searchResults.visibility = View.VISIBLE
    }


    //      ----------------------------------------------------------------------------------------

    override fun onDestroyView() {
        super.onDestroyView()
        searchTextWatcher?.let { binding.searchQuery.removeTextChangedListener(it) }
        _binding = null
    }

}