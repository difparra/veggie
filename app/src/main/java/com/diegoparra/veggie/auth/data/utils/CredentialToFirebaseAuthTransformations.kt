package com.diegoparra.veggie.auth.data.utils

import android.net.Uri
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.GoogleAuthProvider

object CredentialToFirebaseAuthTransformations {

    fun GoogleSignInAccount.getAuthCredential(): AuthCredential {
        return GoogleAuthProvider.getCredential(this.idToken, null)
    }

    fun GoogleSignInAccount.getName(): String? {
        return this.displayName
    }

    fun GoogleSignInAccount.getPhoto(): Uri? {
        return this.photoUrl
    }

    fun LoginResult.getAuthCredential(): AuthCredential {
        return FacebookAuthProvider.getCredential(this.accessToken.token)
    }

}