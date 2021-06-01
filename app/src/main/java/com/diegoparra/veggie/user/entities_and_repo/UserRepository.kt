package com.diegoparra.veggie.user.entities_and_repo

import com.diegoparra.veggie.core.Either
import com.diegoparra.veggie.core.Failure
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    fun isSignedIn(): Flow<Boolean>
    fun signOut()

    fun getBasicUserInfo(): Flow<Either<Failure, BasicUserInfo>>
    suspend fun getSignInMethodsForEmail(email: String): Either<Failure, List<SignInMethod>>

    suspend fun signUpWithEmailAndPassword(user: User, password: String): Either<Failure, Unit>
    suspend fun signInWithEmailAndPassword(email: String, password: String): Either<Failure, Unit>
    suspend fun sendPasswordResetEmail(email: String): Either<Failure, Unit>

    suspend fun signInWithGoogleAccount(account: GoogleSignInAccount): Either<Failure, Unit>


}

/*
    USER SCREENS

    Title: name, email, image
    Orders / Complete your profile

    Profile: Name, email, phoneNumber, links accounts
    Orders
    Address
    *Payment
    LogOut

    *Contact us


    Additional:
    * Coins / Coupons
    Invite friend
    Contact us
    About

 */