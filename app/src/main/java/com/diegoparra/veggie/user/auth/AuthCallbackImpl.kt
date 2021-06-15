package com.diegoparra.veggie.user.auth

import android.net.Uri
import com.diegoparra.veggie.auth.utils.AuthCallbacks
import com.diegoparra.veggie.auth.domain.Profile
import com.diegoparra.veggie.auth.domain.SignInMethod
import com.diegoparra.veggie.core.kotlin.Either
import com.diegoparra.veggie.core.kotlin.Failure
import com.diegoparra.veggie.user.UserRepository
import timber.log.Timber
import javax.inject.Inject

class AuthCallbackImpl @Inject constructor(
    private val userRepository: UserRepository
) : AuthCallbacks {

    override suspend fun onUserSignedIn(
        profile: Profile,
        signInMethod: SignInMethod
    ): Either<Failure, Unit> {
        return userRepository.updateUserData(
            userId = profile.id,
            email = profile.email,
            name = profile.name
        )
    }

    override suspend fun onUserSignedUp(
        profile: Profile,
        signInMethod: SignInMethod
    ): Either<Failure, Unit> {
        return Either.Right(Unit)
    }

    override suspend fun onPhoneVerified(
        userId: String,
        phoneNumber: String
    ): Either<Failure, Unit> {
        return userRepository.updateUserData(userId = userId, phoneNumber = phoneNumber)
    }

    override suspend fun onUpdateProfile(
        userId: String,
        name: String?,
        photoUrl: Uri?
    ): Either<Failure, Unit> {
        if (name != null || photoUrl != null) {
            return userRepository.updateUserData(userId = userId, name = name)
        } else {
            Timber.w("Nothing to update.")
            return Either.Right(Unit)
        }
    }

    override suspend fun onSignOut(userId: String) {
        /* For now, there is nothing to do when user sign out */
    }

}