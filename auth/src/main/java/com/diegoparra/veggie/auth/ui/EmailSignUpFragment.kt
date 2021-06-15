package com.diegoparra.veggie.auth.ui

import android.os.Bundle
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.diegoparra.veggie.auth.databinding.FragmentEmailSignUpBinding
import com.diegoparra.veggie.auth.ui.utils.AuthResultNavigation
import com.diegoparra.veggie.auth.ui.utils.handleError
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

    private var emailTextWatcher: TextWatcher? = null
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

        binding.btnSignUp.setOnClickListener {
            it.hideKeyboard()
            viewModel.signUp(
                email = binding.email.text.toString(),
                password = binding.password.text.toString(),
                name = binding.name.text.toString()
            )
        }

        emailTextWatcher = binding.email.addTextChangedListenerDistinctChanged {
            Timber.d("email changed to: ${it.toString()}")
            viewModel.setEmail(it.toString())
        }
        passwordTextWatcher = binding.password.addTextChangedListenerDistinctChanged {
            Timber.d("password changed to: ${it.toString()}")
            viewModel.setPassword(it.toString())
        }
        nameTextWatcher = binding.name.addTextChangedListenerDistinctChanged {
            Timber.d("name changed to: ${it.toString()}")
            viewModel.setName(it.toString())
        }
    }

    private fun subscribeUi() {
        subscribeEmail()
        subscribePassword()
        subscribeName()
        subscribeToastMessage()
        subscribeValidationFailure()
        subscribeNavigateSignedIn()
    }

    private fun subscribeEmail() {
        viewModel.email.observe(viewLifecycleOwner) {
            Timber.d("email resource received: $it")
            binding.emailLayout.handleError(resource = it, femaleGenderString = false)
        }
    }

    private fun subscribePassword() {
        viewModel.password.observe(viewLifecycleOwner) {
            Timber.d("password resource received: $it")
            binding.passwordLayout.handleError(resource = it, femaleGenderString = true)
        }
    }

    private fun subscribeName() {
        viewModel.name.observe(viewLifecycleOwner) {
            Timber.d("name resource received: $it")
            binding.nameLayout.handleError(resource = it, femaleGenderString = false)
        }
    }

    private fun subscribeToastMessage() {
        viewModel.toastFailure.observe(viewLifecycleOwner, EventObserver {
            Timber.d("toastMessage failure received: $it")
            Snackbar.make(binding.root, it.toString(), Snackbar.LENGTH_SHORT).show()
        })
    }

    private fun subscribeValidationFailure() {
        viewModel.btnContinueEnabled.observe(viewLifecycleOwner) {
            Timber.d("btnContinueEnabled received: $it")
            binding.btnSignUp.isEnabled = !it
        }
    }

    private fun subscribeNavigateSignedIn() {
        viewModel.navigateSignedIn.observe(viewLifecycleOwner, EventObserver {
            Timber.d("navigateSignedIn received: $it")
            AuthResultNavigation.setResultAndNavigate(findNavController(), it)
        })
    }


    override fun onDestroyView() {
        super.onDestroyView()
        emailTextWatcher?.let { binding.email.removeTextChangedListener(it) }
        passwordTextWatcher?.let { binding.password.removeTextChangedListener(it) }
        nameTextWatcher?.let { binding.name.removeTextChangedListener(it) }
        _binding = null
    }

}