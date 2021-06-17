package com.diegoparra.veggie.order.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ConcatAdapter
import com.diegoparra.veggie.databinding.FragmentShippingInfoBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ShippingInfoFragment : Fragment() {

    private var _binding: FragmentShippingInfoBinding? = null
    private val binding get() = _binding!!

    private val headerAdapter by lazy {
        ShippingHeaderAdapter(
            object : ShippingHeaderAdapter.OnItemClickListener {
                override fun onPhoneNumberClick() {
                    //  TODO: onPhoneNumberClick
                    Snackbar.make(
                        binding.root,
                        "TODO: Open edit phone number",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }

                override fun onAddressClick() {
                    //  TODO: onAddressClick
                    Snackbar.make(binding.root, "TODO: Open edit address", Snackbar.LENGTH_SHORT)
                        .show()
                }
            }
        )
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

        val concatAdapter = ConcatAdapter(headerAdapter)
        binding.recyclerShippingInfo.setHasFixedSize(true)
        binding.recyclerShippingInfo.adapter = concatAdapter
    }

    private fun subscribeUi() {
        //  TODO:   Add viewModel
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}