package com.diegoparra.veggie.auth.data.firebase

import android.net.Uri

data class ProfileInfoUpdateFirebase(
    val name: String? = null,
    val photoUrl: Uri? = null
)