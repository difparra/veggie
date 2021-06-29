package com.diegoparra.veggie.order.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import com.diegoparra.veggie.R
import com.diegoparra.veggie.core.android.EventObserver
import com.diegoparra.veggie.core.formatApp
import com.diegoparra.veggie.core.kotlin.Failure
import com.diegoparra.veggie.core.kotlin.Resource
import com.diegoparra.veggie.core.kotlin.addPriceFormat
import com.diegoparra.veggie.databinding.FragmentOrderSummaryBinding
import com.diegoparra.veggie.order.viewmodels.OrderViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OrderSummaryFragment: Fragment() {

    private var _binding: FragmentOrderSummaryBinding? = null
    private val binding get() = _binding!!
    private val viewModel: OrderViewModel by hiltNavGraphViewModels(R.id.nav_order)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOrderSummaryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        subscribeUi()
        binding.btnBack.setOnClickListener { findNavController().popBackStack() }
        binding.totalProducts.setOnClickListener {
            val action = OrderSummaryFragmentDirections.actionOrderSummaryFragmentToProductsOrderSummaryFragment()
            findNavController().navigate(action)
        }
        binding.btnOrder.setOnClickListener {
            viewModel.sendOrder()
        }
        //  Initial state for loading layout. It is intended to use just when pressing sendOrder button.
        with(binding.loadingLayout) {
            if(isVisible)   isVisible = false
        }
    }

    @SuppressLint("SetTextI18n")
    private fun subscribeUi() {
        viewModel.name.observe(viewLifecycleOwner) {
            binding.userName.text = it
        }
        viewModel.phoneNumber.observe(viewLifecycleOwner) {
            binding.userPhoneNumber.text = it
        }
        viewModel.address.observe(viewLifecycleOwner) {
            binding.deliveryAddress.text = it?.fullAddress()
        }
        viewModel.deliverySchedule.observe(viewLifecycleOwner) {
            it?.let {
                binding.deliveryDateTime.text =
                    it.date.formatApp(short = true) + "; " + Pair(it.timeRange.from, it.timeRange.to).formatApp()
            }
        }
        //  Must observe this, in order to have some value for the liveData and make the makeOrder
        //  button valid (it needs this products)
        //  When not observing this, button will not work until user has opened
        //  ProductsOrderSummaryFragment which attach an observer to viewModel
        viewModel.productsList.observe(viewLifecycleOwner) {}
        viewModel.total.observe(viewLifecycleOwner) {
            it?.let {
                binding.totalSubtotalValue.text = it.subtotal.addPriceFormat()
                binding.totalDeliveryValue.text = it.deliveryCost.addPriceFormat()
                binding.totalTotalValue.text = it.total.addPriceFormat()
            }
        }

        viewModel.sendOrderResult.observe(viewLifecycleOwner, EventObserver {
            if(it == null) {
                //  Value has not been initialized
                return@EventObserver
            }
            when(it) {
                is Resource.Loading ->
                    binding.loadingLayout.isVisible = true
                is Resource.Success -> {
                    binding.loadingLayout.isVisible = false
                    navigateOnOrderSentSuccessfully(it.data)
                }
                is Resource.Error -> {
                    binding.loadingLayout.isVisible = false
                    renderFailureSendingOrder(it.failure)
                }
            }
        })
    }

    private fun navigateOnOrderSentSuccessfully(orderId: String) {
        OrderResultNavigation.setResult(navController = findNavController(), result = true)
        val action = OrderSummaryFragmentDirections.actionOrderSummaryFragmentToOrderSendResultDialog(
            success = true,
            successString = orderId,
            failureString = null
        )
        findNavController().navigate(action)
    }

    private fun renderFailureSendingOrder(failure: Failure) {
        OrderResultNavigation.setResult(navController = findNavController(), result = false)
        val action = OrderSummaryFragmentDirections.actionOrderSummaryFragmentToOrderSendResultDialog(
            success = true,
            successString = null,
            failureString = failure.toString()
        )
        findNavController().navigate(action)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}