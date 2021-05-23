package com.diegoparra.veggie.user.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.diegoparra.veggie.core.Resource
import com.diegoparra.veggie.databinding.FragmentEmailSignInBinding
import com.diegoparra.veggie.user.viewmodels.EmailAuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EmailSignInFragment : Fragment() {

    private var _binding : FragmentEmailSignInBinding? = null
    private val binding get() = _binding!!
    //private val viewModel: EmailAuthViewModel by hiltNavGraphViewModels(R.id.nav_sign_in)
    private val viewModel: EmailAuthViewModel by viewModels()
    //private val viewModel: EmailAuthViewModel by navGraphViewModels(R.id.nav_main)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEmailSignInBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.email.observe(viewLifecycleOwner) {
            binding.email.setText(it)
        }
        binding.email.isEnabled = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}