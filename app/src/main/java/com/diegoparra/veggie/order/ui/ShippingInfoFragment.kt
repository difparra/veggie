package com.diegoparra.veggie.order.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.diegoparra.veggie.databinding.FragmentShippingInfoBinding
import com.diegoparra.veggie.order.domain.DeliverySchedule
import com.diegoparra.veggie.order.domain.TimeRange
import com.diegoparra.veggie.order.viewmodels.ShippingInfoViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.time.LocalDate
import java.time.LocalTime

@AndroidEntryPoint
class ShippingInfoFragment : Fragment() {

    private var _binding: FragmentShippingInfoBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ShippingInfoViewModel by viewModels()
    private val adapter by lazy {
        ShippingScheduleAdapter { date: LocalDate, timeRange: TimeRange ->
            Timber.d("Date selected: date=$date, from=${timeRange.from}, to=${timeRange.to}",)
            viewModel.selectDeliverySchedule(
                DeliverySchedule(
                    date = date,
                    timeRange = timeRange
                )
            )
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
            //  TODO:   Redirect to addPhoneNumber flow
            Snackbar.make(binding.root, "TODO: Open edit phone number", Snackbar.LENGTH_SHORT)
                .show()
        }

        binding.address.setOnClickListener {
            //  TODO:   Redirect to address flow
            Snackbar.make(binding.root, "TODO: Open edit address", Snackbar.LENGTH_SHORT).show()
        }

        binding.recyclerShippingSchedule.setHasFixedSize(true)
        binding.recyclerShippingSchedule.adapter = adapter

    }

    private fun subscribeUi() {
        viewModel.isAuthenticated.observe(viewLifecycleOwner) {
            //  TODO:   Redirect to signInFlow
            Timber.d("isAuthenticated = $it")
            if(!it) {
                findNavController().popBackStack()
                Snackbar.make(binding.root, "User is not authenticated.", Snackbar.LENGTH_SHORT).show()
            }
        }

        viewModel.phoneNumber.observe(viewLifecycleOwner) {
            Timber.d("phoneNumber = $it")
            binding.phoneNumber.setText(it)
        }

        viewModel.address.observe(viewLifecycleOwner) {
            Timber.d("address = ${it?.fullAddress()}")
            binding.address.setText(it?.fullAddress())
        }

        viewModel.deliveryCosts.observe(viewLifecycleOwner) {
            val listToSubmit = it.map {
                ShippingScheduleAdapter.Item.ShippingItem(
                    date = it.schedule.date,
                    timeRange = it.schedule.timeRange,
                    cost = it.cost,
                    isSelected = it.isSelected
                )
            }.addHeaders()
            Timber.d("listToSubmit = ${listToSubmit.map { 
                when(it) {
                    is ShippingScheduleAdapter.Item.ShippingItem ->
                        "date = ${it.date}, from = ${it.timeRange.from}, to = ${it.timeRange.to}, cost = ${it.cost}, selected = ${it.isSelected}"
                    is ShippingScheduleAdapter.Item.Header ->
                        "date = ${it.date}"    
                }
            }.joinToString("\n")}")
            adapter.submitList(listToSubmit)
        }
    }

    private fun List<ShippingScheduleAdapter.Item.ShippingItem>.addHeaders() : List<ShippingScheduleAdapter.Item> {
        val list = mutableListOf<ShippingScheduleAdapter.Item>()
        var currentDay: LocalDate? = null
        this.forEach {
            if (it.date != currentDay) {
                list.add(ShippingScheduleAdapter.Item.Header(it.date))
                currentDay = it.date
            }
            list.add(it)
        }
        return list
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}