package com.diegoparra.veggie.user.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.diegoparra.veggie.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_user, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //  TODO: ViewModel and navigate only if user has not signed in
        findNavController().navigate(UserFragmentDirections.actionNavUserToNavSignIn())
        /*  //  Set in the navGraph xml. It is better there.
        findNavController().navigate(
            UserFragmentDirections.actionNavUserToNavSignIn(),
            navOptions { popUpTo(R.id.nav_user) { inclusive = true } }
        )*/
    }

}