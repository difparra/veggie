package com.diegoparra.veggie.user.data

import com.diegoparra.veggie.core.Either
import com.diegoparra.veggie.core.Failure
import com.diegoparra.veggie.core.SignInFailure
import com.diegoparra.veggie.user.entities_and_repo.BasicUserInfo
import com.diegoparra.veggie.user.entities_and_repo.SignInMethod
import com.diegoparra.veggie.user.entities_and_repo.User
import com.google.firebase.auth.*
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
            //  TODO:   Check if information is correct
            //          Correct photoUri when signing in with facebook, is the only one that is not correct
            val profile = providerData.find { it.providerId == providerId }
            Timber.d("""
                completeProviderData = 
                 providerIds = ${providerData.map { it.providerId }.joinToString()}
                 emails = ${providerData.map { it.email }.joinToString()}
                 names = ${providerData.map { it.displayName }.joinToString()}
                 photoUrls = ${providerData.map { it.photoUrl }.joinToString()}
            """.trimIndent())
            Timber.d("""
                profile = $profile
                Profile Info: providerId = ${profile?.providerId}, email = ${profile?.email}, name = ${profile?.displayName}, photoUri = ${profile?.photoUrl} 
            """.trimIndent())
            Timber.d("""
                user normal: uid = ${this.uid} -> $this
                userInfo = providerId = ${this.providerId}, email = ${this.email}, name = ${this.displayName}, photoUri = ${this.photoUrl}
            """.trimIndent())
            val user = BasicUserInfo(
                id = this.uid,
                email = this.email!!,
                name = profile?.displayName ?: this.displayName ?: email!!.substringBefore('@'),
                photoUrl = profile?.photoUrl ?: this.photoUrl
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
        return when (this) {
            EmailAuthProvider.EMAIL_PASSWORD_SIGN_IN_METHOD -> SignInMethod.EMAIL
            GoogleAuthProvider.GOOGLE_SIGN_IN_METHOD -> SignInMethod.GOOGLE
            FacebookAuthProvider.FACEBOOK_SIGN_IN_METHOD -> SignInMethod.FACEBOOK
            else -> null
        }
    }

}