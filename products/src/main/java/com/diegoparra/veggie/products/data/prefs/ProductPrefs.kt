package com.diegoparra.veggie.products.data.prefs

import com.diegoparra.veggie.core.kotlin.BasicTime

interface ProductPrefs {

    suspend fun saveTagsUpdatedAt(value: BasicTime)
    suspend fun getTagsUpdatedAt(): BasicTime?

    suspend fun saveProductsUpdatedAt(value: BasicTime)
    suspend fun getProductsUpdatedAt(): BasicTime?

}