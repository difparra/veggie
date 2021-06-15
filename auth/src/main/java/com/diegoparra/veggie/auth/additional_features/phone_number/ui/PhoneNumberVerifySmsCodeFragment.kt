package com.diegoparra.veggie.auth.additional_features.phone_number.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.diegoparra.veggie.auth.R
import com.diegoparra.veggie.auth.additional_features.phone_number.ui.PhoneNumberViewModel
import com.diegoparra.veggie.auth.additional_features.phone_number.ui.PhoneResultNavigation
import com.diegoparra.veggie.auth.databinding.FragmentPhoneNumberVerifySmsCodeBinding
import com.diegoparra.veggie.auth.utils.AuthFailure
import com.diegoparra.veggie.core.android.EventObserver
import com.diegoparra.veggie.core.android.hideKeyboard
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PhoneNumberVerifySmsCodeFragment : Fragment() {

    private var _binding: FragmentPhoneNumberVerifySmsCodeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PhoneNumberViewModel by hiltNavGraphViewModels(R.id.nav_verify_phone_number)
    private val args: PhoneNumberVerifySmsCodeFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPhoneNumberVerifySmsCodeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        subscribeUi()

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.description.text =
            getString(R.string.verify_phone_number_description, args.phoneNumber)

        binding.resendCode.setOnClickListener {
            it.hideKeyboard()
            viewModel.resendVerificationCode(requireActivity())
        }

        binding.btnVerify.setOnClickListener {
            it.hideKeyboard()
            viewModel.verifyPhoneNumberWithSmsCode(binding.smsCode.text.toString())
        }
    }

    private fun subscribeUi() {
        viewModel.loading.observe(viewLifecycleOwner) {
            binding.progressBar.isVisible = it
        }
        viewModel.failure.observe(viewLifecycleOwner, EventObserver {
            val message = when(it) {
                is AuthFailure.PhoneAuthFailures.InvalidSmsCode -> getString(R.string.failure_incorrect_field_m, getString(R.string.sms_code))
                is AuthFailure.PhoneAuthFailures.ExpiredSmsCode -> getString(R.string.failure_code_has_expired)
                else -> it.toString()
            }
            Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
        })
        viewModel.navigateSuccess.observe(viewLifecycleOwner, EventObserver {
            PhoneResultNavigation.setResultAndNavigate(navController = findNavController(), result = it)
        })
        viewModel.codeSent.observe(viewLifecycleOwner, EventObserver {
            Snackbar.make(binding.root, R.string.code_has_been_resent, Snackbar.LENGTH_SHORT).show()
        })
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}