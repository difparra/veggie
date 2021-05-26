package com.diegoparra.veggie.products.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.diegoparra.veggie.R
import com.diegoparra.veggie.core.Failure
import com.diegoparra.veggie.core.Resource
import com.diegoparra.veggie.databinding.FragmentHomeBinding
import com.diegoparra.veggie.products.domain.Tag
import com.diegoparra.veggie.products.viewmodels.TagsViewModel
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    //  TODO:   Add banner

    private var _binding : FragmentHomeBinding? = null
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
            when(it){
                is Resource.Loading -> {
                    binding.progressBar.isVisible = true
                    binding.tabLayout.isVisible = false
                    binding.viewPager.isVisible = false
                    binding.errorText.isVisible = false
                }
                is Resource.Success -> {
                    binding.progressBar.isVisible = false
                    binding.tabLayout.isVisible = true
                    binding.viewPager.isVisible = true
                    binding.errorText.isVisible = false
                    renderTags(it.data)
                }
                is Resource.Error -> {
                    binding.progressBar.isVisible = false
                    binding.tabLayout.isVisible = false
                    binding.viewPager.isVisible = false
                    binding.errorText.isVisible = true
                    renderFailure(it.failure)
                }
            }
        }
    }


    private fun renderTags(tags: List<Tag>){
        val adapter = TabsAdapter(this, tags.map { it.id })
        binding.viewPager.adapter = adapter
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = tags[position].name
        }.attach()
    }

    private fun renderFailure(failure: Failure){
        when(failure){
            is Failure.ProductsFailure.TagsNotFound ->
                binding.errorText.text = getString(R.string.failure_no_tags)
            else ->
                binding.errorText.text = failure.toString()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}