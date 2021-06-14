package com.diegoparra.veggie.user.auth.domain

import android.net.Uri
import com.diegoparra.veggie.core.Either
import com.diegoparra.veggie.core.Failure

interface AuthCallbacks {

    suspend fun onUserSignedIn(profile: Profile, signInMethod: SignInMethod): Either<Failure, Unit>
    suspend fun onUserSignedUp(profile: Profile, signInMethod: SignInMethod): Either<Failure, Unit>
    suspend fun onPhoneVerified(userId: String, phoneNumber: String): Either<Failure, Unit>
    suspend fun onUpdateProfile(
        userId: String,
        name: String? = null,
        photoUrl: Uri? = null
    ): Either<Failure, Unit>

}