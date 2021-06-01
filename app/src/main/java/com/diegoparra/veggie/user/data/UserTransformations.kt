package com.diegoparra.veggie.user.data

import com.diegoparra.veggie.core.Either
import com.diegoparra.veggie.core.Failure
import com.diegoparra.veggie.core.SignInFailure
import com.diegoparra.veggie.user.entities_and_repo.BasicUserInfo
import com.diegoparra.veggie.user.entities_and_repo.SignInMethod
import com.diegoparra.veggie.user.entities_and_repo.User
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import timber.log.Timber

object UserTransformations {

    fun FirebaseUser?.toIsSignedIn(): Boolean {
        return this != null && !isAnonymous
    }

    fun FirebaseUser?.toBasicUserInfo(): Either<Failure, BasicUserInfo> {
        return if (this == null) {
            Either.Left(SignInFailure.SignInState.NotSignedIn)
        } else if (this.isAnonymous) {
            Either.Left(SignInFailure.SignInState.Anonymous)
        } else {
            val user = BasicUserInfo(
                id = this.uid,
                email = this.email!!,
                name = this.displayName ?: this.email!!.substringBefore('@'),
                photoUrl = this.photoUrl
            )
            Either.Right(user)
        }
    }

    fun List<String>.toSignInMethodList(): List<SignInMethod> {
        if (this.isNullOrEmpty()) {
            return emptyList()
        }
        return this
            .mapNotNull { it.toSignInMethod() }
    }

    fun String.toSignInMethod(): SignInMethod? {
        return when(this) {
            EmailAuthProvider.EMAIL_PASSWORD_SIGN_IN_METHOD -> SignInMethod.EMAIL
            GoogleAuthProvider.GOOGLE_SIGN_IN_METHOD -> SignInMethod.GOOGLE
            FacebookAuthProvider.FACEBOOK_SIGN_IN_METHOD -> SignInMethod.FACEBOOK
            else -> null
        }
    }

}