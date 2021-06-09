package com.diegoparra.veggie.auth.domain

object AuthConstants {
    const val LOGIN_SUCCESSFUL: String = "LOGIN_SUCCESSFUL"
}


enum class SignInMethod {
    EMAIL,
    GOOGLE,
    FACEBOOK,
    UNKNOWN;

    //  Companion object is also useful to define static functions using extension functions
    companion object {
        fun valueOfOrUnknown(value: String): SignInMethod {
            return try {
                valueOf(value.uppercase())
            } catch (e: IllegalArgumentException) {
                UNKNOWN
            }
        }
    }

    override fun toString(): String {
        return name[0] + name.substring(1).lowercase()
    }
}