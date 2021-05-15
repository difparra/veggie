package com.diegoparra.veggie.products.data.products.prefs

interface ProductPrefs {

    suspend fun saveTagsUpdatedAt(value: Long)
    suspend fun getTagsUpdatedAt() : Long

    //fun getProductsLastUpdatedAt() : Long

}