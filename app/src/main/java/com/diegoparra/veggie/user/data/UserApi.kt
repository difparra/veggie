package com.diegoparra.veggie.user.data

import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class UserApi @Inject constructor(
    private val auth: FirebaseAuth
) {

    fun isSignedIn() : Boolean {
        val user = auth.currentUser
        return user != null && !user.isAnonymous
    }



}