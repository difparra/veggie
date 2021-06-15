package com.diegoparra.veggie.user.address.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import com.diegoparra.veggie.R
import com.diegoparra.veggie.core.kotlin.Resource
import com.diegoparra.veggie.core.android.EventObserver
import com.diegoparra.veggie.core.android.getDimensionFromAttr
import com.diegoparra.veggie.core.android.getFloatFromAttr
import com.diegoparra.veggie.databinding.FragmentAddressBinding
import com.diegoparra.veggie.user.address.domain.Address
import com.diegoparra.veggie.user.address.domain.AddressConstants
import com.diegoparra.veggie.core.kotlin.runIfTrue
import com.diegoparra.veggie.user.address.viewmodels.AddressViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class AddressFragment : Fragment() {

    private var _binding: FragmentAddressBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AddressViewModel by hiltNavGraphViewModels(R.id.nav_user_address)
    private val mapAddressToRadioButtonId: MutableMap<String, Int> = mutableMapOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val navController = findNavController()
        val addressFragmentAsBackStackEntry =
            navController.getBackStackEntry(R.id.addressFragment_navAddress)
        val savedStateHandle = addressFragmentAsBackStackEntry.savedStateHandle
        savedStateHandle.getLiveData<Boolean>(AddressConstants.ADDRESS_ADDED_SUCCESSFULLY)
            .observe(addressFragmentAsBackStackEntry) {
                Timber.d("${AddressConstants.ADDRESS_ADDED_SUCCESSFULLY} = $it")
                it.runIfTrue { viewModel.refreshData() }
            }
    }

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
            val action = AddressFragmentDirections.actionAddressFragmentNavAddressToAddressAddFragmentNavAddress()
            findNavController().navigate(action)
        }
    }

    private fun subscribeUi() {
        viewModel.addressList.observe(viewLifecycleOwner) {
            binding.addressRadioGroup.removeAllViews()
            mapAddressToRadioButtonId.clear()
            when (it) {
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
        viewModel.selectedAddressId.observe(viewLifecycleOwner) {
            val radioButtonId = mapAddressToRadioButtonId[it]
            radioButtonId?.let { binding.addressRadioGroup.check(it) }
        }
        viewModel.failure.observe(viewLifecycleOwner, EventObserver {
            Snackbar.make(binding.root, it.toString(), Snackbar.LENGTH_SHORT).show()
        })
    }

    private fun renderAddressList(addressList: List<Address>) {
        addressList.forEach { address ->
            val radioButton = createRadioButton()
            mapAddressToRadioButtonId[address.id] = radioButton.id
            radioButton.text = getAddressString(address)
            radioButton.setOnClickListener { viewModel.selectAddress(address.id) }
            radioButton.setOnLongClickListener { showActionsDialogForAddress(address); true }
            /*
            //  Could be needed if selectedAddressId observer was already called, before
            //  radioButtons being created, but it is currently working without it.
            viewModel.selectedAddressId.value?.let {
                if(it == address.id) { radioButton.isChecked = true }
            }*/
            binding.addressRadioGroup.addView(radioButton)
        }
    }

    private fun createRadioButton(): RadioButton {
        val paddingSmall = requireContext().getDimensionFromAttr(R.attr.paddingSmall)
        val paddingStandard = requireContext().getDimensionFromAttr(R.attr.paddingStandard)
        val radioButton = RadioButton(context)
        radioButton.id = View.generateViewId()
        radioButton.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        radioButton.setPadding(
            paddingSmall, paddingStandard, 0, paddingStandard
        )
        return radioButton
    }

    private fun getAddressString(address: Address): String {
        return address.address + (address.details?.let { "\n" + it } ?: "")
    }

    private fun showActionsDialogForAddress(address: Address) {
        val action = AddressFragmentDirections.actionAddressFragmentNavAddressToAddressActionsDialogFragmentNavAddress(
            addressId = address.id,
            addressString = getAddressString(address)
        )
        findNavController().navigate(action)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}