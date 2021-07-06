package com.diegoparra.veggie.auth.ui

import android.os.Bundle
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.diegoparra.veggie.auth.databinding.FragmentEmailBinding
import com.diegoparra.veggie.auth.viewmodels.EmailViewModel
import com.diegoparra.veggie.core.android.*
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EmailFragment : Fragment() {

    private var _binding: FragmentEmailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: EmailViewModel by viewModels()
    private var emailTextWatcher: TextWatcher? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEmailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        subscribeUi()

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
        emailTextWatcher = binding.email.addTextChangedListenerDistinctChanged {
            viewModel.setEmail(email = it.toString())
        }
        binding.email.setOnEnterListener(binding.btnContinue)
        binding.btnContinue.setOnClickListener {
            it.hideKeyboard()
            viewModel.onContinueClicked(email = binding.email.text.toString())
        }
    }

    private fun subscribeUi() {
        viewModel.email.observe(viewLifecycleOwner) {
            binding.emailLayout.setErrorMessageFromResource(resource = it)
        }
        viewModel.loading.observe(viewLifecycleOwner) {
            binding.progressBar.isVisible = it
        }
        viewModel.btnContinueEnabled.observe(viewLifecycleOwner) {
            binding.btnContinue.isEnabled = it
        }
        viewModel.toastFailure.observe(viewLifecycleOwner, EventObserver {
            Snackbar.make(
                binding.root,
                it.getContextMessage(binding.root.context),
                Snackbar.LENGTH_SHORT
            ).show()
        })
        viewModel.navigateEmailAuthMethod.observe(viewLifecycleOwner, EventObserver {
            val action = if (it.isNewUser) {
                EmailFragmentDirections.actionEmailFragmentToEmailSignUpFragment(email = it.email)
            } else {
                EmailFragmentDirections.actionEmailFragmentToEmailSignInFragment(email = it.email)
            }
            findNavController().navigate(action)
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        emailTextWatcher?.let { binding.email.removeTextChangedListener(it) }
        _binding = null
    }

}