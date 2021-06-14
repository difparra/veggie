package com.diegoparra.veggie.user.auth.domain

import android.net.Uri

/**
 * Basic user info when authenticated
 */
data class Profile(
    val id: String,
    val email: String,
    val name: String,
    val photoUrl: Uri?,
    val phoneNumber: String?
)