package com.diegoparra.veggie.products.data.products.room

import androidx.room.TypeConverter

class ProductsConverters {

    @TypeConverter
    fun fromStringList(value: List<String>?) : String? {
        return value?.joinToString(separator = ",")
    }

    @TypeConverter
    fun toStringList(string: String?) : List<String>? {
        return string?.split(',')
    }

}