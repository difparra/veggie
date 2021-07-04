package com.diegoparra.veggie.order.data.room

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class OrderConverters {

    @TypeConverter
    fun fromMap(value: Map<String, String>?) : String? {
        val type = object : TypeToken<Map<String, String>>() {}.type
        return Gson().toJson(value, type)
    }

    @TypeConverter
    fun toMap(string: String?) : Map<String, String>? {
        val type = object  : TypeToken<Map<String, String>>() {}.type
        return Gson().fromJson<Map<String, String>>(string, type)
    }

}