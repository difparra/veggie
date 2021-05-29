package com.diegoparra.veggie.user.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.diegoparra.veggie.R
import com.diegoparra.veggie.core.EventObserver
import com.diegoparra.veggie.core.SignInFailure
import com.diegoparra.veggie.databinding.FragmentEmailSignUpBinding
import com.diegoparra.veggie.user.viewmodels.EmailSignUpViewModel
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
            viewModel.signUp(
                email = binding.email.text.toString(),
                password = binding.password.text.toString(),
                name = binding.name.text.toString()
            )
        }

        emailTextWatcher = binding.email.addTextChangedListener {
            Timber.d("email changed to: ${it.toString()}")
            viewModel.setEmail(it.toString())
        }
        passwordTextWatcher = binding.password.addTextChangedListener {
            Timber.d("password changed to: ${it.toString()}")
            viewModel.setPassword(it.toString())
        }
        nameTextWatcher = binding.name.addTextChangedListener {
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
            binding.emailLayout.handleError(
                resource = it, femaleGenderString = false
            )
        }
    }

    private fun subscribePassword() {
        viewModel.password.observe(viewLifecycleOwner) {
            Timber.d("password resource received: $it")
            binding.passwordLayout.handleError(
                resource = it, femaleGenderString = true
            )
        }
    }

    private fun subscribeName() {
        viewModel.name.observe(viewLifecycleOwner) {
            Timber.d("name resource received: $it")
            binding.nameLayout.handleError(
                resource = it, femaleGenderString = false
            )
        }
    }

    private fun subscribeToastMessage() {
        viewModel.toastFailure.observe(viewLifecycleOwner, EventObserver {
            Timber.d("toastMessage failure received: $it")
            Toast.makeText(binding.root.context, it.toString(), Toast.LENGTH_SHORT).show()
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
            setLoginResultAndNavigate(findNavController(), true)
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