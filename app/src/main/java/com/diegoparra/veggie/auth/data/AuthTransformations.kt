package com.diegoparra.veggie.auth.data

import android.net.Uri
import com.diegoparra.veggie.core.Either
import com.diegoparra.veggie.core.Failure
import com.diegoparra.veggie.core.SignInFailure
import com.diegoparra.veggie.auth.domain.Profile
import com.diegoparra.veggie.auth.domain.SignInMethod
import com.google.firebase.auth.*

object AuthTransformations {

    fun FirebaseUser?.isSignedIn(): Boolean {
        return this != null && !isAnonymous
    }

    fun FirebaseUser?.toProfile(
        signedInWith: SignInMethod?,
        fbToken: String?
    ): Either<Failure, Profile> {
        return if (this == null) {
            Either.Left(SignInFailure.SignInState.NotSignedIn)
        } else if (this.isAnonymous) {
            Either.Left(SignInFailure.SignInState.Anonymous)
        } else {
            val providerProfile = this.providerData.find {
                SignInMethod.fromProviderId(it.providerId) == signedInWith
            }
            val photoUrl = if (providerProfile != null && signedInWith == SignInMethod.FACEBOOK) {
                Uri.parse("${providerProfile.photoUrl}?access_token=$fbToken")
            } else {
                providerProfile?.photoUrl ?: photoUrl
            }

            val profile = Profile(
                id = uid,
                email = email ?: providerProfile?.email ?: "",
                name = displayName ?: providerProfile?.displayName ?: email?.substringBefore('@') ?: "User",
                photoUrl = photoUrl
            )
            Either.Right(profile)
        }
    }


    fun SignInMethod.Companion.fromSignInMethod(signInMethodFirebase: String): SignInMethod {
        return when (signInMethodFirebase) {
            EmailAuthProvider.EMAIL_PASSWORD_SIGN_IN_METHOD -> SignInMethod.EMAIL
            GoogleAuthProvider.GOOGLE_SIGN_IN_METHOD -> SignInMethod.GOOGLE
            FacebookAuthProvider.FACEBOOK_SIGN_IN_METHOD -> SignInMethod.FACEBOOK
            else -> SignInMethod.UNKNOWN
        }
    }

    private fun SignInMethod.Companion.fromProviderId(providerIdFirebase: String): SignInMethod {
        return when (providerIdFirebase) {
            EmailAuthProvider.PROVIDER_ID -> SignInMethod.EMAIL
            GoogleAuthProvider.PROVIDER_ID -> SignInMethod.GOOGLE
            FacebookAuthProvider.PROVIDER_ID -> SignInMethod.FACEBOOK
            else -> SignInMethod.UNKNOWN
        }
    }

}