package com.diegoparra.veggie.user.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.diegoparra.veggie.R
import com.diegoparra.veggie.core.Either
import com.diegoparra.veggie.core.Failure
import com.diegoparra.veggie.databinding.FragmentEmailBinding
import com.diegoparra.veggie.user.entities_and_repo.IsEmailRegistered
import com.diegoparra.veggie.user.viewmodels.EmailAuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class EmailFragment : Fragment() {

    private var _binding : FragmentEmailBinding? = null
    private val binding get() = _binding!!
    //private val viewModel: EmailAuthViewModel by hiltNavGraphViewModels(R.id.nav_sign_in)
    private val viewModel: EmailAuthViewModel by viewModels()
    //private val viewModel: EmailAuthViewModel by navGraphViewModels(R.id.nav_main)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEmailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.btnContinue.setOnClickListener {
            val email = binding.email.text.toString().trim()
            validateEmailAndNavigate(email)
        }
    }

    private fun validateEmailAndNavigate(email: String) {
        val liveData = viewModel.validateAndSetEmail(email)
        liveData.observe(viewLifecycleOwner) {
            when(it){
                is Either.Left -> displayErrorMessage(it.a)
                is Either.Right -> {
                    binding.emailLayout.error = null
                    navigate(it.b)
                }
            }
            /*
                Very important:
                ->  Need to remove liveData observer, otherwise, there could be more than one liveData
                attached, and when entering a successful value, navigate would be called more than once
                (one for each active liveData), and as current destination has changed, when calling the
                navigation from the second liveData, app will crash.
                  An example where more than one liveData can be attached, is entering a invalid email,
                  pressing continue (first live data attached) and then enter the correct email and press
                  continue button again (second live data attached): As view has not been destroyed / navigation
                  has not happen, both live data are active and emitting, so liveData must be detached.
             */
            liveData.removeObservers(viewLifecycleOwner)
        }
    }

    private fun navigate(isEmailRegistered: IsEmailRegistered) {
        val action = when(isEmailRegistered.b){
            true -> EmailFragmentDirections.actionEmailFragmentToEmailSignInFragment()
            false -> EmailFragmentDirections.actionEmailFragmentToEmailSignUpFragment()
        }
        findNavController().navigate(action)
    }

    private fun displayErrorMessage(failure: Failure) {
        val message = when(failure){
            is Failure.SignInFailure.EmptyField -> getString(R.string.failure_empty_field, "Email")
            is Failure.SignInFailure.InvalidEmail -> getString(R.string.failure_invalid_email)
            else -> null
        }
        binding.emailLayout.error = message
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}