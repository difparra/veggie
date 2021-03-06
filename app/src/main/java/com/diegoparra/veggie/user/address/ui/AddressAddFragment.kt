package com.diegoparra.veggie.user.address.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.fragment.findNavController
import com.diegoparra.veggie.core.android.EventObserver
import com.diegoparra.veggie.databinding.FragmentAddressAddBinding
import com.diegoparra.veggie.user.address.domain.AddressConstants
import com.diegoparra.veggie.user.address.viewmodels.AddressAddViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddressAddFragment : Fragment() {

    private var _binding: FragmentAddressAddBinding? = null
    private val binding get() = _binding!!
    private lateinit var prevSavedStateHandle: SavedStateHandle
    private val viewModel: AddressAddViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddressAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setUpAddressNavResult()
        subscribeUi()

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.address.addTextChangedListener {
            //  As soon as user try to edit field, delete error shown by pressing btnSave.
            binding.addressLayout.error = null
        }
        binding.btnSave.setOnClickListener {
            viewModel.saveAddress(
                address = binding.address.text.toString(),
                details = binding.details.text.toString(),
                instructions = binding.instructions.text.toString()
            )
        }
    }

    private fun setUpAddressNavResult() {
        //  Needs to be called in onViewCreated. If called in onCreateView, this method will be
        //  called whenever a configuration change occur, and even if fragment is not visible, so
        //  prevBackStackEntry could return a wrong value. To avoid that, call when fragment is visible.
        val navController = findNavController()
        prevSavedStateHandle = navController.previousBackStackEntry?.savedStateHandle!!
        prevSavedStateHandle.set(AddressConstants.ADDRESS_ADDED_SUCCESSFUL, false)
    }

    private fun subscribeUi() {
        viewModel.navigateSuccess.observe(viewLifecycleOwner, EventObserver {
            prevSavedStateHandle.set(AddressConstants.ADDRESS_ADDED_SUCCESSFUL, it)
            findNavController().popBackStack()
        })

        viewModel.addressFailure.observe(viewLifecycleOwner, EventObserver {
            binding.addressLayout.error = it.getContextMessage(binding.addressLayout.context)
        })

        viewModel.failure.observe(viewLifecycleOwner, EventObserver {
            Snackbar.make(binding.root, it.toString(), Snackbar.LENGTH_SHORT).show()
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}