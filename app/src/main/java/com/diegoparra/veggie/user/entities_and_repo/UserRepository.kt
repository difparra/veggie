package com.diegoparra.veggie.user.entities_and_repo

import com.diegoparra.veggie.core.Either
import com.diegoparra.veggie.core.Failure
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    fun isSignedIn() : Flow<Boolean>
    suspend fun getSignInMethodsForEmail(email: String) : Either<Failure, List<SignInMethod>>



    /*
    Other possible methods:
    suspend fun getLastSignedInEmail() : String?

    suspend fun getCurrentUser() : Either<Failure, User>

    suspend fun signUpWithEmailAndPassword(email: String, password: String)
    suspend fun signInWithEmailAndPassword(email: String, password: String)

    suspend fun signOut()*/

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