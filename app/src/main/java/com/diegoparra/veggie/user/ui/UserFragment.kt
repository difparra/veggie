package com.diegoparra.veggie.user.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.diegoparra.veggie.core.EventObserver
import com.diegoparra.veggie.core.Resource
import com.diegoparra.veggie.databinding.FragmentUserBinding
import com.diegoparra.veggie.user.viewmodels.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserFragment : Fragment() {

    private var _binding: FragmentUserBinding? = null
    private val binding get() = _binding!!
    private val viewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val navController = findNavController()
        val currentBackStackEntry = navController.currentBackStackEntry!!
        val savedStateHandle = currentBackStackEntry.savedStateHandle
        //  Navigate back if login flow was cancelled or failure
        savedStateHandle.getLiveData<Boolean>(SignInOptionsFragment.LOGIN_SUCCESSFUL)
            .observe(currentBackStackEntry) {
                if (!it) {
                    navController.popBackStack()
                }
            }
        //  Navigate to login flow if user is still not signed in
        /*
            TODO:   Navigation error.
                    When entering user and a wrong password, and then rotate the screen, app crash.
         */
        viewModel.isSignedIn.observe(currentBackStackEntry) {
            if (!it) {
                navController.navigate(UserFragmentDirections.actionNavUserToNavSignIn())
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.signOut.setOnClickListener {
            viewModel.signOut()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}