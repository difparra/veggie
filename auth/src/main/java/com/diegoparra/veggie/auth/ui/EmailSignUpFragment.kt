package com.diegoparra.veggie.auth.ui

import android.os.Bundle
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.diegoparra.veggie.auth.databinding.FragmentEmailSignUpBinding
import com.diegoparra.veggie.auth.ui_utils.AuthResultNavigation
import com.diegoparra.veggie.auth.ui_utils.getDefaultErrorMessage
import com.diegoparra.veggie.auth.ui_utils.handleAuthError
import com.diegoparra.veggie.auth.utils.AuthFailure
import com.diegoparra.veggie.auth.viewmodels.EmailSignUpViewModel
import com.diegoparra.veggie.core.android.EventObserver
import com.diegoparra.veggie.core.android.addTextChangedListenerDistinctChanged
import com.diegoparra.veggie.core.android.hideKeyboard
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class EmailSignUpFragment : Fragment() {

    private var _binding: FragmentEmailSignUpBinding? = null
    private val binding get() = _binding!!
    private val viewModel: EmailSignUpViewModel by viewModels()
    private val args: EmailSignUpFragmentArgs by navArgs()

    private var passwordTextWatcher: TextWatcher? = null
    private var nameTextWatcher: TextWatcher? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEmailSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        subscribeUi()

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.email.setText(args.email)
        nameTextWatcher = binding.name.addTextChangedListenerDistinctChanged {
            Timber.d("name changed to: ${it.toString()}")
            viewModel.setName(it.toString())
        }
        passwordTextWatcher = binding.password.addTextChangedListenerDistinctChanged {
            Timber.d("password changed to: ${it.toString()}")
            viewModel.setPassword(it.toString())
        }

        binding.btnSignUp.setOnClickListener {
            Timber.d("onViewCreated() called")
            it.hideKeyboard()
            viewModel.signUp(
                password = binding.password.text.toString(),
                name = binding.name.text.toString()
            )
        }
    }

    private fun subscribeUi() {
        viewModel.name.observe(viewLifecycleOwner) {
            Timber.d("name resource received: $it")
            binding.nameLayout.handleAuthError(resource = it, femaleGenderString = false)
        }
        viewModel.password.observe(viewLifecycleOwner) {
            Timber.d("password resource received: $it")
            binding.passwordLayout.handleAuthError(resource = it, femaleGenderString = true)
        }

        viewModel.btnContinueEnabled.observe(viewLifecycleOwner) {
            Timber.d("btnContinueEnabled received: $it")
            binding.btnSignUp.isEnabled = it
        }
        viewModel.loading.observe(viewLifecycleOwner) {
            binding.progressBar.isVisible = it
        }
        viewModel.navigateSuccess.observe(viewLifecycleOwner, EventObserver {
            Timber.d("navigateSignedIn received: $it")
            AuthResultNavigation.setResultAndNavigate(findNavController(), it)
        })
        viewModel.toastFailure.observe(viewLifecycleOwner, EventObserver {
            Timber.d("toastMessage failure received: $it")
            Snackbar.make(
                binding.root,
                if (it is AuthFailure) it.getDefaultErrorMessage(binding.root.context) else it.toString(),
                Snackbar.LENGTH_SHORT
            ).show()
        })

    }


    override fun onDestroyView() {
        super.onDestroyView()
        passwordTextWatcher?.let { binding.password.removeTextChangedListener(it) }
        nameTextWatcher?.let { binding.name.removeTextChangedListener(it) }
        _binding = null
    }

}