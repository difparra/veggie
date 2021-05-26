package com.diegoparra.veggie.user.ui

import android.os.Bundle
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.diegoparra.veggie.NavSignInDirections
import com.diegoparra.veggie.R
import com.diegoparra.veggie.core.EventObserver
import com.diegoparra.veggie.core.SignInFailure
import com.diegoparra.veggie.databinding.FragmentEmailSignUpBinding
import com.diegoparra.veggie.user.entities_and_repo.UserConstants
import com.diegoparra.veggie.user.viewmodels.EmailSignUpViewModel
import dagger.hilt.android.AndroidEntryPoint

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
                email = binding.email.toString(),
                password = binding.password.toString(),
                name = binding.name.toString()
            )
        }

        binding.email.addTextChangedListener {
            viewModel.setEmail(it.toString())
        }
        binding.password.addTextChangedListener {
            viewModel.setPassword(it.toString())
        }
        binding.name.addTextChangedListener {
            viewModel.setName(it.toString())
        }
    }

    private fun subscribeUi() {
        subscribeEmail()
        subscribePassword()
        subscribeName()
        subscribeToastMessage()
        //subscribeValidationFailure()
        subscribeNavigateSignedIn()
    }

    private fun subscribeEmail() {
        viewModel.email.observe(viewLifecycleOwner) {
            binding.emailLayout.handleError(
                resource = it, femaleGenderString = false,
                otherErrorMessage = { field, failure, _ ->
                    when (failure) {
                        is SignInFailure.WrongSignInMethod.ExistentUser -> getString(
                            R.string.failure_existent_user
                        )
                        is SignInFailure.WrongSignInMethod.SignInMethodNotLinked -> getString(
                            R.string.failure_not_linked_sign_in_method,
                            failure.linkedSignInMethods.joinToString()
                        )
                        else -> failure.toString()
                    }
                }
            )
        }
    }

    private fun subscribePassword() {
        viewModel.password.observe(viewLifecycleOwner) {
            binding.passwordLayout.handleError(
                resource = it, femaleGenderString = true
            )
        }
    }

    private fun subscribeName() {
        viewModel.name.observe(viewLifecycleOwner) {
            binding.nameLayout.handleError(
                resource = it, femaleGenderString = false
            )
        }
    }

    private fun subscribeToastMessage() {
        viewModel.toastFailure.observe(viewLifecycleOwner, EventObserver {
            Toast.makeText(binding.root.context, it.toString(), Toast.LENGTH_SHORT).show()
        })
    }

    private fun subscribeValidationFailure() {
        viewModel.btnContinueEnabled.observe(viewLifecycleOwner) {
            binding.btnSignUp.isEnabled = !it
        }
    }

    private fun subscribeNavigateSignedIn() {
        viewModel.navigateSignedIn.observe(viewLifecycleOwner, EventObserver {
            //  TODO: Not tested yet. Should sign in when correctly authenticated.
            val action = NavSignInDirections.actionPopOutOfSignInFlow()
            findNavController().navigate(action)
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