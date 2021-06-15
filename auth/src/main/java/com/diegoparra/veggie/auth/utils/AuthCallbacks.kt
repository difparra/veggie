package com.diegoparra.veggie.auth.utils

import android.net.Uri
import com.diegoparra.veggie.auth.domain.Profile
import com.diegoparra.veggie.auth.domain.SignInMethod
import com.diegoparra.veggie.core.kotlin.Either
import com.diegoparra.veggie.core.kotlin.Failure

interface AuthCallbacks {

    suspend fun onUserSignedIn(profile: Profile, signInMethod: SignInMethod): Either<Failure, Unit>
    suspend fun onUserSignedUp(profile: Profile, signInMethod: SignInMethod): Either<Failure, Unit>
    suspend fun onPhoneVerified(userId: String, phoneNumber: String): Either<Failure, Unit>
    suspend fun onUpdateProfile(
        userId: String,
        name: String? = null,
        photoUrl: Uri? = null
    ): Either<Failure, Unit>
    suspend fun onSignOut(userId: String)

}