package com.diegoparra.veggie.user.data

import android.net.Uri
import com.diegoparra.veggie.core.Either
import com.diegoparra.veggie.core.Failure
import com.diegoparra.veggie.core.SignInFailure
import com.diegoparra.veggie.user.entities_and_repo.BasicUserInfo
import com.diegoparra.veggie.user.entities_and_repo.SignInMethod
import com.google.firebase.auth.*
import timber.log.Timber

object UserTransformations {

    fun FirebaseUser?.isSignedIn(): Boolean {
        return this != null && !isAnonymous
    }

    fun FirebaseUser?.toBasicUserInfo(signedInWith: SignInMethod?, fbToken: String?): Either<Failure, BasicUserInfo> {
        return if (this == null) {
            Either.Left(SignInFailure.SignInState.NotSignedIn)
        } else if (this.isAnonymous) {
            Either.Left(SignInFailure.SignInState.Anonymous)
        } else {
            Timber.i("All info provider data:")
            providerData.forEach {
                Timber.d("email = ${it.email}, name = ${it.displayName}, photoUrl = ${it.photoUrl}, phoneNumber = ${it.phoneNumber}")
            }
            val profile = this.providerData.find {
                SignInMethod.fromProviderId(it.providerId) == signedInWith
            }
            Timber.i("Selected provider data: ")
            Timber.d("Profile: email = ${profile?.email}, name = ${profile?.displayName}, photoUrl = ${profile?.photoUrl}, phoneNumber = ${profile?.phoneNumber}")
            val photoUrl = if(profile != null && signedInWith == SignInMethod.FACEBOOK){
                Uri.parse("${profile.photoUrl}?access_token=$fbToken")
            } else {
                profile?.photoUrl ?: photoUrl
            }

            val user = BasicUserInfo(
                id = uid,
                email = email ?: profile?.email ?: "",
                name = displayName ?: profile?.displayName ?: email?.substringBefore('@') ?: "User",
                photoUrl = photoUrl
            )
            Timber.d("user = $user")
            Either.Right(user)
        }
    }


    fun SignInMethod.Companion.fromSignInMethod(signInMethodFirebase: String) : SignInMethod {
        return when (signInMethodFirebase) {
            EmailAuthProvider.EMAIL_PASSWORD_SIGN_IN_METHOD -> SignInMethod.EMAIL
            GoogleAuthProvider.GOOGLE_SIGN_IN_METHOD -> SignInMethod.GOOGLE
            FacebookAuthProvider.FACEBOOK_SIGN_IN_METHOD -> SignInMethod.FACEBOOK
            else -> SignInMethod.UNKNOWN
        }
    }

    private fun SignInMethod.Companion.fromProviderId(providerIdFirebase: String) : SignInMethod {
        return when(providerIdFirebase){
            EmailAuthProvider.PROVIDER_ID -> SignInMethod.EMAIL
            GoogleAuthProvider.PROVIDER_ID -> SignInMethod.GOOGLE
            FacebookAuthProvider.PROVIDER_ID -> SignInMethod.FACEBOOK
            else -> SignInMethod.UNKNOWN
        }
    }

}