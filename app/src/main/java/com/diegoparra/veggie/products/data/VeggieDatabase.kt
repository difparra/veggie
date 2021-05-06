package com.diegoparra.veggie.products.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.diegoparra.veggie.products.data.cart.CartDao
import com.diegoparra.veggie.products.data.cart.CartEntity

@Database(entities = [CartEntity::class], version = 1)
abstract class VeggieDatabase : RoomDatabase() {

    companion object {
        const val DB_NAME = "veggiedb"
    }

    abstract fun cartDao() : CartDao

}