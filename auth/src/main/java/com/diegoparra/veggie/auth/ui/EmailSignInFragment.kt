package com.diegoparra.veggie.auth.ui

import android.os.Bundle
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.ViewModelStore
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.diegoparra.veggie.auth.R
import com.diegoparra.veggie.auth.databinding.FragmentEmailSignInBinding
import com.diegoparra.veggie.auth.ui_utils.AuthResultNavigation
import com.diegoparra.veggie.auth.ui_utils.getDefaultErrorMessage
import com.diegoparra.veggie.auth.ui_utils.handleAuthError
import com.diegoparra.veggie.auth.utils.AuthFailure
import com.diegoparra.veggie.auth.viewmodels.EmailSignInViewModel
import com.diegoparra.veggie.core.android.EventObserver
import com.diegoparra.veggie.core.android.addTextChangedListenerDistinctChanged
import com.diegoparra.veggie.core.android.hideKeyboard
import com.diegoparra.veggie.core.kotlin.Resource
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class EmailSignInFragment : Fragment() {

    private var _binding: FragmentEmailSignInBinding? = null
    private val binding get() = _binding!!
    private val viewModel: EmailSignInViewModel by hiltNavGraphViewModels(R.id.nav_sign_in)
    private val args: EmailSignInFragmentArgs by navArgs()
    private var passwordTextWatcher: TextWatcher? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //  Need to set the savedStateHandle of the viewModel because hiltNavGraphViewModels is
        //  being used, so the state handles of the fragment and the viewModels will not be related.
        viewModel.setSavedStateHandle(email = args.email)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEmailSignInBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        subscribeUi()

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.email.setText(args.email)
        passwordTextWatcher = binding.password.addTextChangedListenerDistinctChanged {
            viewModel.setPassword(it.toString())
        }
        binding.forgotPassword.setOnClickListener {
            val action =
                EmailSignInFragmentDirections.actionEmailSignInFragmentToForgotPasswordDialogFragment(email = args.email)
            findNavController().navigate(action)
        }
        binding.btnSignIn.setOnClickListener {
            it.hideKeyboard()
            viewModel.signIn(
                password = binding.password.text.toString()
            )
        }
    }

    private fun subscribeUi() {
        viewModel.password.observe(viewLifecycleOwner) {
            binding.passwordLayout.handleAuthError(resource = it, femaleGenderString = true)
        }

        viewModel.btnContinueEnabled.observe(viewLifecycleOwner) {
            binding.btnSignIn.isEnabled = it
        }
        viewModel.loading.observe(viewLifecycleOwner) {
            binding.progressBar.isVisible = it
        }
        viewModel.navigateSuccess.observe(viewLifecycleOwner, EventObserver {
            AuthResultNavigation.setResultAndNavigate(findNavController(), it)
        })
        viewModel.toastFailure.observe(viewLifecycleOwner, EventObserver {
            Snackbar.make(
                binding.root,
                if (it is AuthFailure) it.getDefaultErrorMessage(binding.root.context) else it.toString(),
                Snackbar.LENGTH_SHORT
            ).show()
        })

        viewModel.passwordResetResult.observe(viewLifecycleOwner, EventObserver {
            Timber.d("passwordResetResult = $it")
            val context = binding.root.context
            val message = when(it) {
                is Resource.Success -> context.getString(R.string.reset_password_email_sent)
                is Resource.Error -> if(it is AuthFailure) it.getDefaultErrorMessage(context) else it.toString()
                is Resource.Loading -> ""
            }
            Snackbar.make(binding.root, "fragmentMessage = $message", Snackbar.LENGTH_SHORT).show()
        })
    }


    override fun onDestroyView() {
        super.onDestroyView()
        passwordTextWatcher?.let { binding.password.removeTextChangedListener(it) }
        _binding = null
    }

}