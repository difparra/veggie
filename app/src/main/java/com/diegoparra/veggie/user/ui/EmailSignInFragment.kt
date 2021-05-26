package com.diegoparra.veggie.user.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.diegoparra.veggie.NavSignInDirections
import com.diegoparra.veggie.R
import com.diegoparra.veggie.core.EventObserver
import com.diegoparra.veggie.core.SignInFailure
import com.diegoparra.veggie.core.SignInFailure.WrongInput.Email
import com.diegoparra.veggie.core.SignInFailure.WrongInput.Password
import com.diegoparra.veggie.databinding.FragmentEmailSignInBinding
import com.diegoparra.veggie.user.viewmodels.EmailSignInViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EmailSignInFragment : Fragment() {

    private var _binding: FragmentEmailSignInBinding? = null
    private val binding get() = _binding!!
    private val viewModel: EmailSignInViewModel by viewModels()
    //private val viewModel: EmailAuthViewModel by hiltNavGraphViewModels(R.id.nav_sign_in)


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
    }


    private fun subscribeUi() {
        subscribeEmail()
        subscribePassword()
        subscribeToastMessage()
        subscribeNavigateSignedIn()
    }

    private fun subscribeEmail() {
        viewModel.email.observe(viewLifecycleOwner) {
            binding.emailLayout.handleError(
                it, mapOf(
                    Email.Empty to getString(
                        R.string.failure_empty_field,
                        getString(R.string.email)
                    ),
                    Email.Invalid to getString(R.string.failure_invalid_email)
                )
            )
        }
    }

    private fun subscribePassword() {
        viewModel.password.observe(viewLifecycleOwner) {
            binding.passwordLayout.handleError(
                it, mapOf(
                    Password.Empty to getString(
                        R.string.failure_empty_field,
                        getString(R.string.password)
                    )
                )
            )
        }
    }

    private fun subscribeToastMessage() {
        viewModel.toastFailure.observe(viewLifecycleOwner, EventObserver {
            val message = when (it) {
                is SignInFailure.NewUser -> getString(R.string.failure_new_user)
                is SignInFailure.SignInMethodNotLinked -> getString(
                    R.string.failure_not_linked_sign_in_method,
                    it.linkedSignInMethods.joinToString()
                )
                else -> it.toString()
            }
            Toast.makeText(binding.root.context, message, Toast.LENGTH_SHORT).show()
        })
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
        _binding = null
    }

}