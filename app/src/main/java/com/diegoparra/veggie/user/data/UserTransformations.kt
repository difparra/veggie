package com.diegoparra.veggie.user.data

import com.diegoparra.veggie.user.entities_and_repo.SignInMethod
import com.diegoparra.veggie.user.entities_and_repo.User
import com.google.firebase.auth.FirebaseUser

object UserTransformations {

    fun List<String>.toSignInMethodList() : List<SignInMethod> {
        if(this.isNullOrEmpty()){
            return emptyList()
        }
        return this.mapNotNull {
            when(it){
                "email" -> SignInMethod.EMAIL
                "google" -> SignInMethod.GOOGLE
                "facebook" -> SignInMethod.FACEBOOK
                else -> null
            }
        }
    }





    /*//  TODO()
    fun FirebaseUser.toUser() : User = User(
        id = uid,
        email = email ?: "",
        name = displayName ?: email ?: "",
        phoneNumber = phoneNumber,
        address = null,
        photoUrl = null
    )*/

}