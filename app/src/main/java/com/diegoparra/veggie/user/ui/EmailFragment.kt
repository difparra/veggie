package com.diegoparra.veggie.user.ui

import android.os.Bundle
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
            val result = viewModel.setEmail(
                binding.email.text.toString().trim()
            )
            when(result){
                is Either.Right ->{
                    binding.emailLayout.error = null
                    //  Always navigate to sign in. In signIn check and if there is no account created
                    //  with that email, send to create account / signUp.
                    findNavController().navigate(EmailFragmentDirections.actionEmailFragmentToEmailSignInFragment())
                }
                is Either.Left -> {
                    val message = when(result.a){
                        is Failure.SignInFailure.EmptyField -> getString(R.string.failure_empty_field, "Email")
                        is Failure.SignInFailure.InvalidEmail -> getString(R.string.failure_invalid_email)
                        else -> null
                    }
                    binding.emailLayout.error = message
                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}