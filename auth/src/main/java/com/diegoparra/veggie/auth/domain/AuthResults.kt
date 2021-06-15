package com.diegoparra.veggie.auth.domain

data class AuthResults(
    val profile: Profile,
    val isNewUser: Boolean,
    val signInMethod: SignInMethod
)