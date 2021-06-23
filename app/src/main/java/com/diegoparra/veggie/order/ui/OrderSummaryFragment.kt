package com.diegoparra.veggie.order.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import com.diegoparra.veggie.R
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
    }

    private fun subscribeUi() {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}