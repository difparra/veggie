package com.diegoparra.veggie.order.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.diegoparra.veggie.R
import com.diegoparra.veggie.core.kotlin.Failure
import com.diegoparra.veggie.core.kotlin.Resource
import com.diegoparra.veggie.databinding.FragmentOrdersHistoryBinding
import com.diegoparra.veggie.order.domain.Order
import com.diegoparra.veggie.order.viewmodels.OrdersHistoryViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OrdersHistoryFragment : Fragment() {

    private var _binding: FragmentOrdersHistoryBinding? = null
    private val binding get() = _binding!!
    private val viewModel: OrdersHistoryViewModel by viewModels()
    private val adapter by lazy { OrdersListAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOrdersHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        subscribeUi()

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.ordersList.setHasFixedSize(true)
        binding.ordersList.adapter = adapter
    }

    private fun subscribeUi() {
        viewModel.ordersList.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading ->
                    setViewsVisibility(loading = true, mainViews = false, errorViews = false)
                is Resource.Success -> {
                    setViewsVisibility(loading = false, mainViews = true, errorViews = false)
                    renderOrdersList(it.data)
                }
                is Resource.Error -> {
                    setViewsVisibility(loading = false, mainViews = false, errorViews = true)
                    renderFailure(it.failure)
                }
            }
        }
    }

    private fun setViewsVisibility(loading: Boolean, mainViews: Boolean, errorViews: Boolean) {
        binding.progressBar.isVisible = loading
        binding.ordersList.isVisible = mainViews
        binding.errorText.isVisible = errorViews
    }

    private fun renderOrdersList(ordersList: List<Order>) {
        if (ordersList.isEmpty()) {
            adapter.submitList(listOf())
            binding.errorText.text = getString(R.string.no_orders_yet)
            binding.errorText.isVisible = true
        } else {
            adapter.submitList(ordersList)
        }
    }

    private fun renderFailure(failure: Failure) {
        binding.errorText.text = failure.toString()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}