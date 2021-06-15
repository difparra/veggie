package com.diegoparra.veggie.auth.ui

import android.os.Bundle
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import com.diegoparra.veggie.auth.R
import com.diegoparra.veggie.auth.databinding.FragmentEmailSignInBinding
import com.diegoparra.veggie.auth.ui.utils.AuthResultNavigation
import com.diegoparra.veggie.auth.ui.utils.handleError
import com.diegoparra.veggie.auth.viewmodels.EmailSignInViewModel
import com.diegoparra.veggie.core.android.EventObserver
import com.diegoparra.veggie.core.android.addTextChangedListenerDistinctChanged
import com.diegoparra.veggie.core.android.hideKeyboard
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EmailSignInFragment : Fragment() {

    private var _binding: FragmentEmailSignInBinding? = null
    private val binding get() = _binding!!
    private val viewModel: EmailSignInViewModel by hiltNavGraphViewModels(R.id.nav_sign_in)

    private var emailTextWatcher: TextWatcher? = null
    private var passwordTextWatcher: TextWatcher? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEmailSignInBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        subscribeUi()

        binding.btnSignIn.setOnClickListener {
            it.hideKeyboard()
            viewModel.signIn(
                email = binding.email.text.toString(),
                password = binding.password.text.toString()
            )
        }
        binding.forgotPassword.setOnClickListener {
            val action =
                EmailViewPagerFragmentDirections.actionEmailViewPagerFragmentToForgotPasswordFragment()
            findNavController().navigate(action)
        }

        emailTextWatcher = binding.email.addTextChangedListenerDistinctChanged {
            viewModel.setEmail(it.toString())
        }
        passwordTextWatcher = binding.password.addTextChangedListenerDistinctChanged {
            viewModel.setPassword(it.toString())
        }
    }

    private fun subscribeUi() {
        subscribeEmail()
        subscribePassword()
        subscribeToastMessage()
        subscribeValidationFailure()
        subscribeNavigateSignedIn()
    }

    private fun subscribeEmail() {
        viewModel.email.observe(viewLifecycleOwner) {
            binding.emailLayout.handleError(resource = it, femaleGenderString = false)
        }
    }

    private fun subscribePassword() {
        viewModel.password.observe(viewLifecycleOwner) {
            binding.passwordLayout.handleError(resource = it, femaleGenderString = true)
        }
    }

    private fun subscribeToastMessage() {
        viewModel.toastFailure.observe(viewLifecycleOwner, EventObserver {
            Snackbar.make(binding.root, it.toString(), Snackbar.LENGTH_SHORT).show()
        })
    }

    private fun subscribeValidationFailure() {
        viewModel.btnContinueEnabled.observe(viewLifecycleOwner) {
            binding.btnSignIn.isEnabled = !it
        }
    }

    private fun subscribeNavigateSignedIn() {
        viewModel.navigateSignedIn.observe(viewLifecycleOwner, EventObserver {
            AuthResultNavigation.setResultAndNavigate(findNavController(), it)
        })
    }


    override fun onDestroyView() {
        super.onDestroyView()
        emailTextWatcher?.let { binding.email.removeTextChangedListener(it) }
        passwordTextWatcher?.let { binding.password.removeTextChangedListener(it) }
        _binding = null
    }

}