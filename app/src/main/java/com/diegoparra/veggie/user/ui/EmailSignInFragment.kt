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
import com.diegoparra.veggie.core.Resource
import com.diegoparra.veggie.core.SignInFailure.WrongSignInMethod
import com.diegoparra.veggie.databinding.FragmentEmailSignInBinding
import com.diegoparra.veggie.user.entities_and_repo.UserConstants
import com.diegoparra.veggie.user.viewmodels.EmailSignInViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EmailSignInFragment : Fragment() {

    private var _binding: FragmentEmailSignInBinding? = null
    private val binding get() = _binding!!
    private val viewModel: EmailSignInViewModel by viewModels()

    //private val viewModel: EmailAuthViewModel by hiltNavGraphViewModels(R.id.nav_sign_in)
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
            viewModel.signIn(
                email = binding.email.text.toString(),
                password = binding.password.text.toString()
            )
        }
        binding.forgotPassword.setOnClickListener {
            //  TODO: Deal with forgotPassword flow
            Toast.makeText(it.context, "TODO()", Toast.LENGTH_SHORT).show()
        }

        emailTextWatcher = binding.email.addTextChangedListener {
            viewModel.setEmail(it.toString())
        }
        passwordTextWatcher = binding.password.addTextChangedListener {
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
            binding.emailLayout.handleError(
                resource = it, femaleGenderString = false,
                otherErrorMessage = { field, failure, _ ->
                    when (failure) {
                        is WrongSignInMethod.NewUser -> getString(R.string.failure_new_user)
                        is WrongSignInMethod.SignInMethodNotLinked -> getString(
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

    private fun subscribeToastMessage() {
        viewModel.toastFailure.observe(viewLifecycleOwner, EventObserver {
            Toast.makeText(binding.root.context, it.toString(), Toast.LENGTH_SHORT).show()
        })
    }

    private fun subscribeValidationFailure() {
        viewModel.btnContinueEnabled.observe(viewLifecycleOwner) {
            binding.btnSignIn.isEnabled = !it
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
        _binding = null
    }

}