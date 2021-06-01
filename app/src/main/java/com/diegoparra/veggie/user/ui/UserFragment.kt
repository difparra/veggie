package com.diegoparra.veggie.user.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import coil.load
import com.diegoparra.veggie.R
import com.diegoparra.veggie.core.EventObserver
import com.diegoparra.veggie.databinding.FragmentUserBinding
import com.diegoparra.veggie.user.viewmodels.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class UserFragment : Fragment() {

    private var _binding: FragmentUserBinding? = null
    private val binding get() = _binding!!
    private val viewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        //  Important: onCreate is called every time screen rotates. It does not matter view is visible,
        //  as long as it is in the backStack.
        super.onCreate(savedInstanceState)
        val navController = findNavController()
        val userFragmentAsBackStackEntry = navController.getBackStackEntry(R.id.nav_user)
            //  Must use nav_destination_id, because when rotating screen in SignInOptionsFragment,
            //  as UserFragment is in the backStack, onCreate will be recalled, but this time
            //  currentBackStackEntry is not userFragment but signInOptions. It will cause some error.
        val savedStateHandle = userFragmentAsBackStackEntry.savedStateHandle
        //  Navigate back if login flow was cancelled or failure
        savedStateHandle.getLiveData<Boolean>(SignInOptionsFragment.LOGIN_SUCCESSFUL)
            .observe(userFragmentAsBackStackEntry) {
                //  OK, it is just being called when view is visible, not only if rotating device.
                Timber.d("LOGIN_SUCCESSFUL = $it")
                if (!it) {
                    navController.popBackStack()
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
        //  Observer should be attached to the view, not to the backStackEntry, as the next screens
        //  can modify the authState, and as this observer would be active, it will try to navigate
        //  when the original screen is not correct and app will crash.
        viewModel.isSignedIn.observe(viewLifecycleOwner, EventObserver{
            if (!it) {
                findNavController().navigate(UserFragmentDirections.actionNavUserToNavSignIn())
            }
        })

        viewModel.name.observe(viewLifecycleOwner) {
            binding.name.text = it
        }
        viewModel.email.observe(viewLifecycleOwner) {
            binding.email.text = it
        }
        viewModel.photoUrl.observe(viewLifecycleOwner) {
            if(it != null) {
                binding.photo.isVisible = true
                binding.photo.load(it)
            }else{
                binding.photo.isVisible = false
            }
        }

        binding.signOut.setOnClickListener {
            viewModel.signOut()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}