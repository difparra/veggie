package com.diegoparra.veggie.user.auth

import android.net.Uri
import com.diegoparra.veggie.core.Either
import com.diegoparra.veggie.core.Failure
import com.diegoparra.veggie.core.suspendFlatMap
import com.diegoparra.veggie.user.UserRepository
import com.diegoparra.veggie.user.auth.domain.AuthCallbacks
import com.diegoparra.veggie.user.auth.domain.Profile
import com.diegoparra.veggie.user.auth.domain.SignInMethod
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

}