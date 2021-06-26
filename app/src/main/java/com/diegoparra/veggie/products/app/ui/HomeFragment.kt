package com.diegoparra.veggie.products.app.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.diegoparra.veggie.R
import com.diegoparra.veggie.core.kotlin.Failure
import com.diegoparra.veggie.core.kotlin.Resource
import com.diegoparra.veggie.databinding.FragmentHomeBinding
import com.diegoparra.veggie.products.domain.Tag
import com.diegoparra.veggie.products.app.viewmodels.TagsViewModel
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    //  TODO:   Add banner

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TagsViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.tags.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading ->
                    setViewsVisibility(loadingViews = true, successViews = false, errorViews = false)
                is Resource.Success -> {
                    setViewsVisibility(loadingViews = false, successViews = true, errorViews = false)
                    renderTags(it.data)
                }
                is Resource.Error -> {
                    setViewsVisibility(loadingViews = false, successViews = false, errorViews = true)
                    renderFailure(it.failure)
                }
            }
        }
    }

    private fun setViewsVisibility(loadingViews: Boolean, successViews: Boolean, errorViews: Boolean) {
        binding.progressBar.isVisible = loadingViews
        binding.tabLayout.isVisible = successViews
        binding.viewPager.isVisible = successViews
        binding.errorText.isVisible = errorViews
    }


    private fun renderTags(tags: List<Tag>) {
        //  Dealing with empty list
        if(tags.isEmpty()) {
            binding.errorText.text = getString(R.string.failure_no_tags)
            binding.errorText.isVisible = true
        }

        //  Dealing normal case
        val adapter = TabsAdapter(this, tags.map { it.id })
        binding.viewPager.adapter = adapter
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = tags[position].name
        }.attach()
    }


    private fun renderFailure(failure: Failure) {
        binding.errorText.text = failure.toString()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}