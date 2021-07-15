package com.diegoparra.veggie.products.data.prefs

import com.diegoparra.veggie.core.kotlin.BasicTime

interface ProductPrefs {

    suspend fun saveTagsFetchAt(value: BasicTime)
    suspend fun getTagsFetchAt(): BasicTime?

}