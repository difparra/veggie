package com.diegoparra.veggie.order.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ConcatAdapter
import com.diegoparra.veggie.databinding.FragmentShippingInfoBinding
import com.diegoparra.veggie.order.ui.FakeDeliveryDates.toSubmitList
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import java.time.LocalTime

@AndroidEntryPoint
class ShippingInfoFragment : Fragment() {

    private var _binding: FragmentShippingInfoBinding? = null
    private val binding get() = _binding!!
    private val adapter by lazy {
        ShippingDateTimeAdapter { date: LocalDate, from: LocalTime, to: LocalTime ->
            //  TODO
            Snackbar.make(
                binding.root,
                "TODO: date selected: date=$date, from=$from, to=$to",
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentShippingInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        subscribeUi()

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.phoneNumber.setOnClickListener {
            Snackbar.make(binding.root, "TODO: Open edit phone number", Snackbar.LENGTH_SHORT)
                .show()
        }

        binding.address.setOnClickListener {
            Snackbar.make(binding.root, "TODO: Open edit address", Snackbar.LENGTH_SHORT).show()
        }

        binding.recyclerShippingDate.setHasFixedSize(true)
        binding.recyclerShippingDate.adapter = adapter

    }

    private fun subscribeUi() {
        //  TODO:   Add viewModel
        adapter.submitList(FakeDeliveryDates.delivery.toSubmitList())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}