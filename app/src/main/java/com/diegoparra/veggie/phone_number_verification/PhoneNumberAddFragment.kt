package com.diegoparra.veggie.phone_number_verification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.fragment.findNavController
import com.diegoparra.veggie.NavVerifyPhoneNumberDirections
import com.diegoparra.veggie.R
import com.diegoparra.veggie.core.*
import com.diegoparra.veggie.databinding.FragmentPhoneNumberAddBinding
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
        PhoneResultNav.setPreviousDestinationAsOriginal(navController)
        PhoneResultNav.setResult(navController, false)
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
            PhoneResultNav.setResultAndNavigate(navController = findNavController(), result = it)
        })
    }

    private fun subscribeStates() {
        viewModel.loading.observe(viewLifecycleOwner) {
            binding.progressBar.isVisible = it
        }
        viewModel.failure.observe(viewLifecycleOwner, EventObserver {
            val message = when (it) {
                is Failure.PhoneAuthFailures.InvalidRequest -> getString(R.string.failure_invalid_request_phone_number)
                is Failure.PhoneAuthFailures.TooManyRequests -> getString(R.string.failure_too_many_requests)
                is SignInFailure.WrongInput -> getString(R.string.failure_invalid_request_phone_number)
                else -> it.toString()
            }
            Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}