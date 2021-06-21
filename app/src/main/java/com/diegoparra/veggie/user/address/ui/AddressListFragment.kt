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
import com.diegoparra.veggie.core.kotlin.Failure
import com.diegoparra.veggie.user.address.domain.Address
import com.diegoparra.veggie.user.address.domain.AddressConstants
import com.diegoparra.veggie.core.kotlin.runIfTrue
import com.diegoparra.veggie.databinding.FragmentAddressListBinding
import com.diegoparra.veggie.user.address.viewmodels.AddressListViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class AddressListFragment : Fragment() {

    private var _binding: FragmentAddressListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AddressListViewModel by hiltNavGraphViewModels(R.id.nav_user_address)
    private val mapAddressToRadioButtonId: MutableMap<String, Int> = mutableMapOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val navController = findNavController()
        val addressFragmentAsBackStackEntry =
            navController.getBackStackEntry(R.id.addressListFragment)
        val savedStateHandle = addressFragmentAsBackStackEntry.savedStateHandle
        savedStateHandle.getLiveData<Boolean>(AddressConstants.ADDRESS_ADDED_SUCCESSFUL)
            .observe(addressFragmentAsBackStackEntry) {
                Timber.d("${AddressConstants.ADDRESS_ADDED_SUCCESSFUL} = $it")
                it.runIfTrue {
                    viewModel.refreshAddressList()
                    //  When adding an address useCase will also set as main, so adding an address
                    //  also implies that the selectedAddress has changed and result of nav_address
                    //  should be true
                    AddressResultNavigation.setResult(navController = navController, result = true)
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddressListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setUpToSendBackLoginResult()
        subscribeUi()
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.btnAddAddress.setOnClickListener {
            val action =
                AddressListFragmentDirections.actionAddressFragmentNavAddressToAddressAddFragmentNavAddress()
            findNavController().navigate(action)
        }
    }

    private fun setUpToSendBackLoginResult() {
        val navController = findNavController()
        AddressResultNavigation.setPreviousDestinationAsOriginal(navController)
        //AddressResultNavigation.setResult(navController, result = false)
    }

    private fun subscribeUi() {
        viewModel.addressList.observe(viewLifecycleOwner) {
            binding.addressRadioGroup.removeAllViews()
            mapAddressToRadioButtonId.clear()
            when (it) {
                is Resource.Loading -> renderLoadingState()
                is Resource.Success -> renderSuccessState(it.data)
                is Resource.Error -> renderErrorState(it.failure)
            }
        }
        viewModel.selectedAddress.observe(viewLifecycleOwner) {
            val radioButtonId = mapAddressToRadioButtonId[it?.id]
            radioButtonId?.let { binding.addressRadioGroup.check(it) }
            //  TODO:   Check how many times this is calling the live data result in
            //      ShippingInfoFragment, and if more than one, try to fix it, as every call will
            //      also call the repository and database.
            AddressResultNavigation.setResult(navController = findNavController(), result = true)
        }
        viewModel.failure.observe(viewLifecycleOwner, EventObserver {
            Snackbar.make(binding.root, it.toString(), Snackbar.LENGTH_SHORT).show()
        })
    }

    private fun renderLoadingState() {
        binding.progressBar.isVisible = true
        binding.addressRadioGroup.isVisible = false
        binding.btnAddAddress.isVisible = false
        binding.errorText.isVisible = false
    }

    private fun renderErrorState(failure: Failure) {
        binding.progressBar.isVisible = false
        binding.addressRadioGroup.isVisible = false
        binding.btnAddAddress.isVisible = false
        binding.errorText.isVisible = true
        Snackbar.make(binding.root, failure.toString(), Snackbar.LENGTH_SHORT).show()
    }


    //      ADDRESS LIST - SUCCESS STATE    --------------------------------------------------------

    private fun renderSuccessState(addressList: List<Address>) {
        binding.progressBar.isVisible = false
        binding.addressRadioGroup.isVisible = true
        binding.btnAddAddress.isVisible = true
        binding.errorText.isVisible = false
        renderAddressList(addressList = addressList)
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
        return address.fullAddress()
    }

    private fun showActionsDialogForAddress(address: Address) {
        val action =
            AddressListFragmentDirections.actionAddressFragmentNavAddressToAddressActionsDialogFragmentNavAddress(
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