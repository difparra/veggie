package com.diegoparra.veggie.user.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import com.diegoparra.veggie.R
import com.diegoparra.veggie.core.*
import com.diegoparra.veggie.databinding.FragmentEmailBinding
import com.diegoparra.veggie.user.entities_and_repo.IsNewUser
import com.diegoparra.veggie.user.viewmodels.EmailAuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EmailFragment : Fragment() {

    private var _binding: FragmentEmailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: EmailAuthViewModel by hiltNavGraphViewModels(R.id.nav_sign_in)


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
            viewModel.submitEmail(email)
        }

        viewModel.email.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> binding.emailLayout.error = null
                is Resource.Error -> {
                    val message = when (it.failure) {
                        is SignInFailure.WrongInput.EmptyField ->
                            getString(R.string.failure_empty_field, "Email")
                        is SignInFailure.WrongInput.InvalidEmail ->
                            getString(R.string.failure_invalid_email)
                        else -> it.failure.toString()
                    }
                    binding.emailLayout.error = message
                }
            }
        }

        viewModel.toastFailure.observe(viewLifecycleOwner, EventObserver {
            val message = when(it){
                is SignInFailure.SignInMethodNotLinked ->
                    getString(R.string.failure_not_linked_sign_in_method, it.linkedSignInMethods.joinToString())
                else -> it.toString()
            }
            Toast.makeText(binding.root.context, message, Toast.LENGTH_SHORT).show()
        })

        viewModel.navigate.observe(viewLifecycleOwner, EventObserver { isNewUser ->
            val action = when (isNewUser.b) {
                true -> EmailFragmentDirections.actionEmailFragmentToEmailSignUpFragment()
                false -> EmailFragmentDirections.actionEmailFragmentToEmailSignInFragment()
            }
            findNavController().navigate(action)
        })
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}