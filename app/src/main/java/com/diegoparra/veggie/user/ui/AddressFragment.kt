package com.diegoparra.veggie.user.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.diegoparra.veggie.core.Failure
import com.diegoparra.veggie.core.Resource
import com.diegoparra.veggie.databinding.FragmentAddressBinding
import com.diegoparra.veggie.user.domain.Address
import com.diegoparra.veggie.user.viewmodels.AddressViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddressFragment : Fragment() {

    private var _binding: FragmentAddressBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AddressViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddressBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        subscribeUi()
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.btnAddAddress.setOnClickListener {
            //  TODO: Add address functionality
            Snackbar.make(it, "TODO()", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun subscribeUi() {
        viewModel.addressList.observe(viewLifecycleOwner) {
            when(it) {
                is Resource.Loading -> {
                    binding.progressBar.isVisible = true
                    binding.addressRadioGroup.isVisible = false
                    binding.btnAddAddress.isVisible = false
                    binding.errorText.isVisible = false
                }
                is Resource.Success -> {
                    binding.progressBar.isVisible = false
                    binding.addressRadioGroup.isVisible = true
                    binding.btnAddAddress.isVisible = true
                    binding.errorText.isVisible = false
                    renderAddressList(it.data)
                }
                is Resource.Error -> {
                    binding.progressBar.isVisible = false
                    binding.addressRadioGroup.isVisible = false
                    binding.btnAddAddress.isVisible = false
                    binding.errorText.isVisible = true
                    Snackbar.make(binding.root, it.failure.toString(), Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun renderAddressList(addressList: List<Address>) {
        binding.addressRadioGroup.removeAllViews()
        addressList.forEach {
            val radioButton = RadioButton(context)
            radioButton.id = View.generateViewId()
            radioButton.text = it.address + (it.details?.let { "\n" + it } ?: "")
            binding.addressRadioGroup.addView(radioButton)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}