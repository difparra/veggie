package com.diegoparra.veggie.products.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.diegoparra.veggie.core.Failure
import com.diegoparra.veggie.databinding.FragmentHomeBinding
import com.diegoparra.veggie.products.domain.entities.Tag
import com.diegoparra.veggie.products.viewmodels.TagsState
import com.diegoparra.veggie.products.viewmodels.TagsViewModel
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

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

        viewModel.tagsState.observe(viewLifecycleOwner) {
            when(it){
                is TagsState.Loading -> renderLoadingTags()
                is TagsState.Success -> renderTags(it.data)
                is TagsState.EmptyTagsList -> renderEmptyTagsList()
                is TagsState.UnknownError -> renderFailure(it.failure, it.message)
            }
        }
    }

    private fun renderLoadingTags(){
        //  TODO()
    }

    private fun renderTags(tags: List<Tag>){
        val adapter = TabsAdapter(this, tags.map { it.id })
        binding.viewPager.adapter = adapter
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = tags[position].name
        }.attach()
    }

    private fun renderEmptyTagsList(){
        TODO()
    }

    private fun renderFailure(failure: Failure, message: String?){
        TODO()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}