package com.diegoparra.veggie.user.auth.domain

data class AuthResults(
    val profile: Profile,
    val isNewUser: Boolean,
    val signInMethod: SignInMethod
)