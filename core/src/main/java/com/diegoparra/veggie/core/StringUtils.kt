package com.diegoparra.veggie.core

import java.util.*

/**
 *      Remove accents and convert to lower case. Ideal for searches.
 */
fun String.customNormalisation() =
    toLowerCase(Locale.ROOT)
        .replace('á','a').replace('à', 'a')
        .replace('é','e').replace('à', 'a')
        .replace('í','i').replace('à', 'a')
        .replace('ó','o').replace('à', 'a')
        .replace('ú','u').replace('à', 'a')
        .replace('ñ','n')


fun String.trimAllSpaces() = this.replace(" ","")