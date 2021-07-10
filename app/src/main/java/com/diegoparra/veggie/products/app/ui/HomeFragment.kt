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
import timber.log.Timber

@AndroidEntryPoint
class HomeFragment : Fragment() {

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
            //  Set views visibility based on Resource state
            binding.progressBar.isVisible = it is Resource.Loading
            binding.tabLayout.isVisible = it is Resource.Success
            binding.viewPager.isVisible = it is Resource.Success
            binding.errorText.isVisible = it is Resource.Error

            when (it) {
                is Resource.Loading -> {}
                is Resource.Success -> renderTags(it.data)
                is Resource.Error -> renderFailure(it.failure)
            }
        }
    }


    private fun renderTags(tags: List<Tag>) {
        //  Dealing with empty list
        if (tags.isEmpty()) {
            Timber.wtf("Tags list is empty")
            binding.errorText.text = getString(R.string.failure_generic)
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