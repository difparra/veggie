package com.diegoparra.veggie.products.data.prefs

import com.diegoparra.veggie.core.kotlin.BasicTime

interface ProductPrefs {

    suspend fun saveTagsFetchAt(value: BasicTime)
    suspend fun getTagsFetchAt(): BasicTime?

    suspend fun saveProdsSuccessfulFetchAt(value: BasicTime)
    suspend fun getProdsLastSuccessfulFetchAt(): BasicTime?

}