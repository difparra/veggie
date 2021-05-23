package com.diegoparra.veggie.core

import java.util.regex.Pattern

/**
 *      Remove accents and convert to lower case. Ideal for searches.
 */
fun String.removeCaseAndAccents() =
    this.lowercase()
        .replace('á','a').replace('à', 'a')
        .replace('é','e').replace('à', 'a')
        .replace('í','i').replace('à', 'a')
        .replace('ó','o').replace('à', 'a')
        .replace('ú','u').replace('à', 'a')
        .replace('ñ','n')


fun String.trimAllSpaces() = this.replace(" ","")


/**
 * Pattern provided by android to compare EMAIL_ADDRESS
 * Patterns.EMAIL_ADDRESS.matcher(email).matches()
 */
private val EMAIL_ADDRESS_PATTERN = Pattern.compile(
    "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
            "\\@" +
            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
            "(" +
            "\\." +
            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
            ")+"
)

/**
 * Validate is an email address is correct (according to the pattern EMAIL_ADDRESS_PATTERN)
 */
fun validateEmail(email: String) : Boolean =
    email.isNotEmpty() && EMAIL_ADDRESS_PATTERN.matcher(email).matches()