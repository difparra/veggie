package com.diegoparra.veggie.auth.additional_features.phone_number.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import com.diegoparra.veggie.auth.R
import com.diegoparra.veggie.auth.databinding.FragmentPhoneNumberAddBinding
import com.diegoparra.veggie.auth.utils.AuthFailure
import com.diegoparra.veggie.core.android.EventObserver
import com.diegoparra.veggie.core.android.hideKeyboard
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PhoneNumberAddFragment : Fragment() {

    private var _binding: FragmentPhoneNumberAddBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PhoneNumberViewModel by hiltNavGraphViewModels(R.id.nav_verify_phone_number)


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPhoneNumberAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setUpToSendBackPhoneVerificationResult()
        subscribeUi()

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnSendSms.setOnClickListener {
            it.hideKeyboard()
            viewModel.sendVerificationCode(
                phoneNumber = getString(R.string.prefix_phone_number) + binding.phoneNumber.text.toString(),
                activity = requireActivity()
            )
        }
    }

    private fun setUpToSendBackPhoneVerificationResult() {
        //  Initiate phone_verified to false and save the original destination
        val navController = findNavController()
        PhoneResultNavigation.setPreviousDestinationAsOriginal(navController)
        PhoneResultNavigation.setResult(navController, false)
    }

    private fun subscribeUi() {
        subscribeNavigation()
        subscribeStates()
    }

    private fun subscribeNavigation() {
        viewModel.codeSent.observe(viewLifecycleOwner, EventObserver {
            val action =
                PhoneNumberAddFragmentDirections.actionPhoneNumberAddFragmentToPhoneNumberVerifySmsCodeFragment(
                    phoneNumber = it.first,
                    verificationId = it.second
                )
            findNavController().navigate(action)
        })
        viewModel.navigateSuccess.observe(viewLifecycleOwner, EventObserver {
            PhoneResultNavigation.setResultAndNavigate(
                navController = findNavController(),
                result = it
            )
        })
    }

    private fun subscribeStates() {
        viewModel.loading.observe(viewLifecycleOwner) {
            binding.progressBar.isVisible = it
        }
        viewModel.failure.observe(viewLifecycleOwner, EventObserver {
            //  I can use the default messages (got from context) for AuthPhoneFailures.
            Snackbar.make(
                binding.root,
                it.getContextMessage(binding.root.context),
                Snackbar.LENGTH_SHORT
            ).show()
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}