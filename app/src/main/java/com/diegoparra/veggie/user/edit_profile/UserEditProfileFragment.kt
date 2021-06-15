package com.diegoparra.veggie.user.edit_profile

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
import com.diegoparra.veggie.auth.ui.utils.getDefaultWrongInputErrorMessage
import com.diegoparra.veggie.auth.utils.AuthFailure
import com.diegoparra.veggie.core.kotlin.Resource
import com.diegoparra.veggie.core.android.EventObserver
import com.diegoparra.veggie.core.kotlin.runIfTrue
import com.diegoparra.veggie.databinding.FragmentUserEditProfileBinding
import com.diegoparra.veggie.user.edit_profile.auth_phone_number.domain.PhoneConstants
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class UserEditProfileFragment : Fragment() {

    private var _binding: FragmentUserEditProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: EditProfileViewModel by viewModels()
    private var nameTextWatcher: TextWatcher? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val navController = findNavController()
        val userEditProfileFragmentAsBackStackEntry =
            navController.getBackStackEntry(R.id.userEditProfileFragment)
        val savedStateHandle = userEditProfileFragmentAsBackStackEntry.savedStateHandle
        savedStateHandle.getLiveData<Boolean>(PhoneConstants.PHONE_VERIFIED_SUCCESSFUL)
            .observe(userEditProfileFragmentAsBackStackEntry) {
                Timber.d("${PhoneConstants.PHONE_VERIFIED_SUCCESSFUL} = $it")
                it.runIfTrue { viewModel.refreshData(phoneNumber = true) }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUserEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        subscribeInitialValues()
        subscribeUi()

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        nameTextWatcher = binding.name.addTextChangedListener {
            viewModel.setName(it.toString())
        }

        //  It is necessary to set clickable=true and focusable=false on xml in order to get the desired clickListener event.
        binding.phone.setOnClickListener {
            val action =
                UserEditProfileFragmentDirections.actionUserEditProfileFragmentToNavVerifyPhoneNumber()
            findNavController().navigate(action)
        }

        binding.btnSave.setOnClickListener {
            viewModel.save(binding.name.text.toString())
        }
    }

    private fun subscribeInitialValues() {
        viewModel.initialEmail.observe(viewLifecycleOwner, EventObserver {
            binding.email.setText(it)
        })
        viewModel.initialName.observe(viewLifecycleOwner, EventObserver {
            binding.name.setText(it)
        })
        viewModel.initialPhoneNumber.observe(viewLifecycleOwner, EventObserver {
            val phoneNumber = it.replace(getString(R.string.prefix_phone_number), "")
            binding.phone.setText(phoneNumber)
        })
    }

    private fun subscribeUi() {
        viewModel.name.observe(viewLifecycleOwner) {
            with(binding.nameLayout) {
                error = when (it) {
                    is Resource.Success -> null
                    is Resource.Error -> {
                        when (val failure = it.failure) {
                            is AuthFailure.WrongInput -> getDefaultWrongInputErrorMessage(
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
        viewModel.nameIsChanged.observe(viewLifecycleOwner) {
            binding.btnSave.isEnabled = it
        }
        viewModel.failure.observe(viewLifecycleOwner, EventObserver {
            Snackbar.make(binding.root, it.toString(), Snackbar.LENGTH_SHORT).show()
        })
        viewModel.navigateSuccess.observe(viewLifecycleOwner, EventObserver {
            Snackbar.make(
                binding.root,
                R.string.data_has_been_correctly_updated,
                Snackbar.LENGTH_SHORT
            ).show()
            findNavController().popBackStack()
        })
    }


    override fun onDestroyView() {
        super.onDestroyView()
        nameTextWatcher?.let { binding.name.removeTextChangedListener(it) }
        _binding = null
    }

}