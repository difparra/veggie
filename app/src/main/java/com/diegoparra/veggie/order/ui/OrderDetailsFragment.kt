package com.diegoparra.veggie.order.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ConcatAdapter
import com.diegoparra.veggie.R
import com.diegoparra.veggie.databinding.FragmentOrderDetailsBinding
import com.diegoparra.veggie.order.ui.order_flow.OrderProdsAdapter
import com.diegoparra.veggie.order.viewmodels.OrderHistoryViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OrderDetailsFragment: Fragment() {

    private var _binding: FragmentOrderDetailsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: OrderHistoryViewModel by hiltNavGraphViewModels(R.id.nav_user_order)
    private val headerAdapter by lazy { OrderDetailsHeaderAdapter() }
    private val prodsAdapter by lazy { OrderProdsAdapter() }
    private val totalsAdapter by lazy { OrderDetailsTotalsAdapter() }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOrderDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        subscribeUi()

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.orderRecycler.setHasFixedSize(true)
        val concatAdapter = ConcatAdapter(headerAdapter, prodsAdapter, totalsAdapter)
        binding.orderRecycler.adapter = concatAdapter
    }

    private fun subscribeUi() {
        viewModel.selectedOrder.observe(viewLifecycleOwner) {
            binding.title.text = binding.title.context.getString(R.string.order_no, it.id)
            headerAdapter.setOrderDetails(it)
            prodsAdapter.submitList(it.products.products)
            totalsAdapter.setOrderDetails(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}