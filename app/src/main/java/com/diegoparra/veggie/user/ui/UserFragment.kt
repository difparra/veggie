package com.diegoparra.veggie.user.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.diegoparra.veggie.core.Resource
import com.diegoparra.veggie.databinding.FragmentUserBinding
import com.diegoparra.veggie.user.viewmodels.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserFragment : Fragment() {

    private var _binding: FragmentUserBinding? = null
    private val binding get() = _binding!!
    private val viewModel: UserViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        navigateIfNotSignedIn()
    }

    private fun navigateIfNotSignedIn(){
        /*
            In this case there is no need to set navigate as event in viewModel, as navigation
            will pop original destination from backstack. So there is no problem when pressing back
            button, as this fragment will not be navigated with backStack.
        */
        viewModel.isSignedIn.observe(viewLifecycleOwner) {
            if(!it){
                findNavController().navigate(UserFragmentDirections.actionNavUserToNavSignIn())
                /*  //  Set in the navGraph xml. It is better there.
                    findNavController().navigate(
                        UserFragmentDirections.actionNavUserToNavSignIn(),
                        navOptions { popUpTo(R.id.nav_user) { inclusive = true } }
                    )*/
            }else{
                //  Small workaround. This way user is blank while getting signedInState, so if
                //  user is not signedIn, screen does not flash userDataViews and then navigate
                binding.root.isVisible = true
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}