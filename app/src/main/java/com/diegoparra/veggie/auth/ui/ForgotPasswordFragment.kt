package com.diegoparra.veggie.auth.ui

import android.os.Bundle
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import com.diegoparra.veggie.R
import com.diegoparra.veggie.core.*
import com.diegoparra.veggie.databinding.FragmentForgotPasswordBinding
import com.diegoparra.veggie.core.getDefaultWrongInputErrorMessage
import com.diegoparra.veggie.core.getDefaultWrongSignInMethodErrorMessage
import com.diegoparra.veggie.auth.ui.utils.handleError
import com.diegoparra.veggie.auth.viewmodels.EmailSignInViewModel
import com.diegoparra.veggie.core.Fields.EMAIL
import com.google.android.material.snackbar.Snackbar
import timber.log.Timber

class ForgotPasswordFragment : Fragment() {

    private var _binding: FragmentForgotPasswordBinding? = null
    private val binding get() = _binding!!
    private val viewModel: EmailSignInViewModel by hiltNavGraphViewModels(R.id.nav_sign_in)
    private var emailTextWatcher: TextWatcher? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentForgotPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setInitialEmail()
        subscribeUi()

        emailTextWatcher = binding.email.addTextChangedListenerDistinctChanged {
            viewModel.setEmail(it.toString())
        }

        binding.btnSend.setOnClickListener {
            it.hideKeyboard()
            val email = binding.email.text.toString()
            viewModel.sendPasswordResetEmail(email)
        }

        binding.btnBack.setOnClickListener {
            navigateBack()
        }

    }

    private fun setInitialEmail() {
        val initialEmail = viewModel.email.value
        Timber.d("initialEmail = $initialEmail")
        initialEmail?.let {
            val email = when(it){
                is Resource.Success -> it.data
                is Resource.Error -> {
                    when(val failure = it.failure){
                        is SignInFailure.WrongInput -> failure.input
                        is SignInFailure.WrongSignInMethod -> failure.email
                        else -> ""
                    }
                }
                is Resource.Loading -> ""
            }
            binding.email.setText(email)
        }
    }

    private fun subscribeUi() {
        viewModel.email.observe(viewLifecycleOwner) {
            binding.emailLayout.handleError(resource = it, femaleGenderString = false)
        }

        viewModel.passwordResetResult.observe(viewLifecycleOwner, EventObserver {
            when (it) {
                is Resource.Success -> {
                    Snackbar.make(
                        binding.root,
                        R.string.reset_password_email_sent,
                        Snackbar.LENGTH_SHORT
                    ).show()
                    navigateBack()
                }
                is Resource.Error -> {
                    val errorMessage = getPasswordResetFailureMessage(it.failure)
                    Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun navigateBack() {
        try {
            findNavController().popBackStack()
        } catch (e: Exception) {
        }
    }

    private fun getPasswordResetFailureMessage(failure: Failure): String {
        return when (failure) {
            is SignInFailure.WrongInput -> {
                if (failure.field == EMAIL) {
                    getDefaultWrongInputErrorMessage(
                        context = binding.root.context,
                        field = failure.field,
                        failure = failure,
                        femaleString = false
                    )
                } else {
                    Timber.e("It is not supposed to have ${failure.field} errors when trying to send email reset password")
                    failure.toString()
                }
            }
            is SignInFailure.WrongSignInMethod -> {
                getDefaultWrongSignInMethodErrorMessage(
                    context = binding.root.context,
                    failure = failure
                )
            }
            else -> failure.toString()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        emailTextWatcher?.let { binding.email.removeTextChangedListener(it) }
        _binding = null
    }
}