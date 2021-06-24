package com.diegoparra.veggie.order.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import com.diegoparra.veggie.R
import com.diegoparra.veggie.core.appFormat
import com.diegoparra.veggie.databinding.FragmentOrderSummaryBinding
import com.diegoparra.veggie.order.viewmodels.OrderViewModel
import com.google.android.material.snackbar.Snackbar
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
            //  TODO:   Show products in cart
            Snackbar.make(binding.totalProducts, "TODO", Snackbar.LENGTH_SHORT).show()
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
                    it.date.appFormat(short = true) + "; " + Pair(it.timeRange.from, it.timeRange.to).appFormat()
            }
        }
        //  TODO:   Get totals from cart
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}