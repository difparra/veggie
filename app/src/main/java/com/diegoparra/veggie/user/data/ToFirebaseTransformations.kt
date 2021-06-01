package com.diegoparra.veggie.user.data

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.GoogleAuthProvider

object ToFirebaseTransformations {

    fun GoogleSignInAccount.getAuthCredential(): AuthCredential {
        return GoogleAuthProvider.getCredential(this.idToken, null)
    }

    fun GoogleSignInAccount.getProfileInfoFirebase() : ProfileInfoFirebase {
        return ProfileInfoFirebase(
            name = this.displayName ?: this.email?.substringBefore('@'),
            photoUrl = this.photoUrl
        )
    }

}