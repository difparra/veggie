package com.diegoparra.veggie.user.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.fragment.findNavController
import com.diegoparra.veggie.databinding.FragmentSignInOptionsBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class SignInOptionsFragment : Fragment() {

    companion object {
        const val LOGIN_SUCCESSFUL: String = "LOGIN_SUCCESSFUL"
        const val ORIGINAL_DESTINATION: String = "ORIGINAL_DESTINATION"
    }

    private var _binding: FragmentSignInOptionsBinding? = null
    private val binding get() = _binding!!
    private lateinit var savedStateHandle: SavedStateHandle

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSignInOptionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //  Important: Call in onViewCreated. Can't be called in onCreate, because if the device is
        //  rotated, as it is in the backStack and even if it is not visible, onCreate will be
        //  called again and by that time if it is not visible, previousBackStackEntry would have
        //  changed and therefore the incorrect savedStateHandle will be modified.
        setUpToSendBackLoginResult()
        uiSetUp()
    }

    private fun setUpToSendBackLoginResult() {
        val navController = findNavController()
        //  Set login successful to false, so that it knows that login flow was started.
        savedStateHandle = navController.previousBackStackEntry!!.savedStateHandle
        savedStateHandle.set(LOGIN_SUCCESSFUL, false)
        //  Save the original destination in this stateHandle, because it is necessary to modify
        //  its savedStateHandle and send loginResult. It must be done here as there is no
        //  possibility to get a backStackEntry using index nor accessing the backStackIterator.
        navController.currentBackStackEntry?.savedStateHandle?.set(
            ORIGINAL_DESTINATION,
            navController.previousBackStackEntry?.destination?.id!!
        )
    }

    private fun uiSetUp() {
        binding.emailSignIn.setOnClickListener {
            val action =
                SignInOptionsFragmentDirections.actionSignInOptionsFragmentToEmailFragment()
            findNavController().navigate(action)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}