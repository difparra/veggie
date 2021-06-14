package com.diegoparra.veggie

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.diegoparra.veggie.products.cart.data.room.CartDao
import com.diegoparra.veggie.products.cart.data.room.CartEntity
import com.diegoparra.veggie.products.data.room.*

@Database(
    entities = [
        TagEntity::class, MainEntity::class, VariationEntity::class,
        CartEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(ProductConverters::class)
abstract class VeggieDatabase : RoomDatabase() {

    companion object {
        const val DB_NAME = "veggiedb"
    }

    abstract fun productsDao(): ProductsDao
    abstract fun cartDao(): CartDao

}