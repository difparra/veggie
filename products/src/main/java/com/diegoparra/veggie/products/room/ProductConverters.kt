package com.diegoparra.veggie.products.room

import androidx.room.TypeConverter

class ProductConverters {

    @TypeConverter
    fun fromStringList(value: List<String>?) : String? {
        return value?.joinToString(separator = ",")
    }

    @TypeConverter
    fun toStringList(string: String?) : List<String>? {
        return string?.split(',')
    }

}