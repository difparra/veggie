package com.diegoparra.veggie.user.entities_and_repo

import android.net.Uri

data class User (
    val id: String,
    val email: String,
    val name: String,
    val phoneNumber: String? = null,
    val address: String? = null,
    val photoUrl: Uri? = null
)

data class BasicUserInfo(
    val id: String,
    val email: String,
    val name: String,
    val photoUrl: Uri? = null
)


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