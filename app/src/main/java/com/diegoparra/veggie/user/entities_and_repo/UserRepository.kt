package com.diegoparra.veggie.user.entities_and_repo

interface UserRepository {

    suspend fun isSignedIn() : Boolean
    suspend fun getLastSignedInEmail() : String?


    /*suspend fun getCurrentUser() : Either<Failure, User>

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