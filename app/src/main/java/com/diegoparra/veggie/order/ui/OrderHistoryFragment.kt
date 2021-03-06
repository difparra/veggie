package com.diegoparra.veggie.order.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.diegoparra.veggie.R
import com.diegoparra.veggie.core.kotlin.Failure
import com.diegoparra.veggie.core.kotlin.Resource
import com.diegoparra.veggie.databinding.FragmentOrderHistoryBinding
import com.diegoparra.veggie.order.domain.Order
import com.diegoparra.veggie.order.viewmodels.OrderHistoryViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OrderHistoryFragment : Fragment() {

    private var _binding: FragmentOrderHistoryBinding? = null
    private val binding get() = _binding!!
    private val viewModel: OrderHistoryViewModel by hiltNavGraphViewModels(R.id.nav_user_order)
    private val adapter by lazy {
        OrderHistoryAdapter {
            viewModel.selectOrder(orderId = it)
            findNavController().navigate(
                OrderHistoryFragmentDirections.actionOrderHistoryFragmentToOrderDetailsFragment(
                    orderId = it
                )
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOrderHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        subscribeUi()

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.ordersList.setHasFixedSize(true)
        binding.ordersList.addItemDecoration(DividerItemDecoration(binding.root.context, DividerItemDecoration.VERTICAL))
        binding.ordersList.adapter = adapter
    }

    private fun subscribeUi() {
        viewModel.ordersList.observe(viewLifecycleOwner) {
            //  Set views visibility based on Resource state
            binding.progressBar.isVisible = it is Resource.Loading
            binding.ordersList.isVisible = it is Resource.Success
            binding.errorText.isVisible = it is Resource.Error

            when (it) {
                is Resource.Loading -> {}
                is Resource.Success -> renderOrdersList(it.data)
                is Resource.Error -> renderFailure(it.failure)
            }
        }
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