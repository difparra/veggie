package com.diegoparra.veggie.auth.data

import com.diegoparra.veggie.core.Either
import com.diegoparra.veggie.core.Failure
import com.diegoparra.veggie.core.SignInFailure
import com.diegoparra.veggie.auth.domain.Profile
import com.diegoparra.veggie.auth.domain.SignInMethod
import com.google.firebase.auth.*
import timber.log.Timber

object AuthTransformations {

    fun FirebaseUser?.isSignedIn(): Boolean {
        return this != null && !isAnonymous
    }

    fun FirebaseUser?.toProfile(): Either<Failure, Profile> {
        return if (this == null) {
            Either.Left(SignInFailure.SignInState.NotSignedIn)
        } else if (this.isAnonymous) {
            Either.Left(SignInFailure.SignInState.Anonymous)
        } else {
            /*
            photoUrl Facebook=
            https://graph.facebook.com/3903857476336266/picture?access_token=EAAEer5nnCOABABgLFxzUpdPpAOcLGTKAEr78xZB7okXqjqTB3dopFnqMtfblKlMNtzCuQUB4QewLBMZCM9ilWMEEE7Vn0XVfarp4eSvZBR3YdnY4mZC1uZBeOKeBlTx3h9WFzCv9HUZAClO8suB0ftug0zQ88peVjw5fkiZCHWTZA05CSLJi9VzoHnMr3z71Es3opUywNOQ1bHZAlV9HdGpMWIMhBeicTIha7FU3p6dhDqwZDZD
             */
            val profile = Profile(
                id = uid,
                email = email ?: "",
                name = displayName ?: email?.substringBefore('@') ?: "User",
                photoUrl = photoUrl,
                phoneNumber = phoneNumber
            )
            Timber.d("final user sent: $profile")
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