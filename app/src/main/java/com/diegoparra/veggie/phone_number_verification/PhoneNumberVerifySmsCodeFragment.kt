package com.diegoparra.veggie.phone_number_verification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.diegoparra.veggie.NavVerifyPhoneNumberDirections
import com.diegoparra.veggie.R
import com.diegoparra.veggie.core.EventObserver
import com.diegoparra.veggie.core.Failure
import com.diegoparra.veggie.core.hideKeyboard
import com.diegoparra.veggie.databinding.FragmentPhoneNumberVerifySmsCodeBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseException
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

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
                is Failure.PhoneAuthFailures.InvalidSmsCode -> getString(R.string.failure_incorrect_field_m, getString(R.string.sms_code))
                is Failure.PhoneAuthFailures.ExpiredSmsCode -> getString(R.string.failure_code_has_expired)
                else -> it.toString()
            }
            Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
        })
        viewModel.navigateSuccess.observe(viewLifecycleOwner, EventObserver {
            if (it) {
                findNavController().navigate(NavVerifyPhoneNumberDirections.actionNavVerifyPhoneNumberPop())
            }
        })
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}