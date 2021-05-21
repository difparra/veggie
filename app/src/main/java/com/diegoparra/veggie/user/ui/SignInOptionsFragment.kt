package com.diegoparra.veggie.user.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.diegoparra.veggie.databinding.FragmentSignInOptionsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignInOptionsFragment : Fragment() {

    private var _binding : FragmentSignInOptionsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSignInOptionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.emailSignIn.setOnClickListener {
            val action = SignInOptionsFragmentDirections.actionSignInOptionsFragmentToEmailFragment()
            findNavController().navigate(action)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}