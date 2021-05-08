package com.diegoparra.veggie.products.ui

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.diegoparra.veggie.R
import com.diegoparra.veggie.core.Failure
import com.diegoparra.veggie.core.Resource
import com.diegoparra.veggie.core.ResourceViews
import com.diegoparra.veggie.databinding.FragmentSearchBinding
import com.diegoparra.veggie.products.domain.entities.MainProdWithQuantity
import com.diegoparra.veggie.products.ui.utils.hideKeyboard
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
        binding.clearSearchText.visibility = View.GONE
        binding.clearSearchText.setOnClickListener {
            viewModel.clearQuery()
            binding.searchQuery.setText("")
        }
        binding.searchQuery.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                viewModel.setQuery(s.toString())
                val btnClearVisibility = s.toString().isNotEmpty()
                if(binding.clearSearchText.isVisible != btnClearVisibility){
                    binding.clearSearchText.isVisible = btnClearVisibility
                }
            }
        })
        binding.searchQuery.setOnEditorActionListener { v, actionId, event ->
            if(actionId == EditorInfo.IME_ACTION_SEARCH) {
                v.hideKeyboard()
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }
    }



    private fun subscribeUi() {
        val resourceViews = ResourceViews(
                loadingViews = listOf(binding.progressBar),
                successViews = listOf(binding.searchResults),
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
            is Failure.SearchFailure.EmptyQuery -> {
                cleanRecyclerView()
                binding.errorText.text = getString(R.string.failure_empty_search_query)
            }
            is Failure.SearchFailure.NoSearchResults -> {
                cleanRecyclerView()
                binding.errorText.text = getString(R.string.failure_no_search_result)
            }
            else ->
                binding.errorText.text = failure.toString()
        }
    }

    private fun cleanRecyclerView(){
        adapter.submitList(listOf())
        binding.searchResults.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}