package com.diegoparra.veggie.user.ui

import android.os.Bundle
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.diegoparra.veggie.R
import com.diegoparra.veggie.core.getDefaultWrongInputErrorMessage
import com.diegoparra.veggie.core.EventObserver
import com.diegoparra.veggie.core.Resource
import com.diegoparra.veggie.core.SignInFailure
import com.diegoparra.veggie.databinding.FragmentUserEditProfileBinding
import com.diegoparra.veggie.user.viewmodels.EditProfileViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserEditProfileFragment : Fragment() {

    private var _binding: FragmentUserEditProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: EditProfileViewModel by viewModels()
    private var nameTextWatcher: TextWatcher? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUserEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        subscribeUi()

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        nameTextWatcher = binding.name.addTextChangedListener {
            viewModel.setName(it.toString())
        }

        binding.btnSave.setOnClickListener {
            viewModel.save(binding.name.text.toString())
        }
    }

    private fun subscribeUi(){
        viewModel.initialEmail.observe(viewLifecycleOwner, EventObserver {
            binding.email.setText(it)
        })

        viewModel.initialName.observe(viewLifecycleOwner, EventObserver {
            binding.name.setText(it)
        })
        viewModel.initialPhoneNumber.observe(viewLifecycleOwner, EventObserver {
            binding.phone.setText(it)
        })
        viewModel.name.observe(viewLifecycleOwner) {
            with(binding.nameLayout) {
                error = when(it) {
                    is Resource.Success -> {
                        null
                    }
                    is Resource.Error -> {
                        when(val failure = it.failure){
                            is SignInFailure.WrongInput -> getDefaultWrongInputErrorMessage(
                                context, binding.name.hint.toString().lowercase(),
                                failure, false
                            )
                            else -> null
                        }
                    }
                    else -> null
                }
            }
        }
        viewModel.navigateSuccess.observe(viewLifecycleOwner, EventObserver {
            Snackbar.make(binding.root, R.string.data_has_been_correctly_updated, Snackbar.LENGTH_SHORT).show()
            findNavController().popBackStack()
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        nameTextWatcher?.let { binding.name.removeTextChangedListener(it) }
        _binding = null
    }

}