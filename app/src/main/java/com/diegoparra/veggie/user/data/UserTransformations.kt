package com.diegoparra.veggie.user.data

import com.diegoparra.veggie.user.entities_and_repo.SignInMethod
import com.diegoparra.veggie.user.entities_and_repo.User
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import timber.log.Timber

object UserTransformations {

    fun List<String>.toSignInMethodList() : List<SignInMethod> {
        if(this.isNullOrEmpty()){
            return emptyList()
        }
        return this.mapNotNull {
            when(it){
                EmailAuthProvider.EMAIL_PASSWORD_SIGN_IN_METHOD -> SignInMethod.EMAIL
                GoogleAuthProvider.GOOGLE_SIGN_IN_METHOD -> SignInMethod.GOOGLE
                FacebookAuthProvider.FACEBOOK_SIGN_IN_METHOD -> SignInMethod.FACEBOOK
                else -> null
            }
        }
    }

}