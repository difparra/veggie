package com.diegoparra.veggie.user.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import com.diegoparra.veggie.R
import com.diegoparra.veggie.core.Resource
import com.diegoparra.veggie.databinding.FragmentEmailSignUpBinding
import com.diegoparra.veggie.user.viewmodels.EmailAuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EmailSignUpFragment : Fragment() {

    private var _binding : FragmentEmailSignUpBinding? = null
    private val binding get() = _binding!!
    private val viewModel: EmailAuthViewModel by hiltNavGraphViewModels(R.id.nav_sign_in)
    

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEmailSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.email.observe(viewLifecycleOwner) {
            if(it is Resource.Success){
                binding.email.setText(it.data)
                binding.email.isEnabled = false
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}