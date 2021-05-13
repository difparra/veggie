package com.diegoparra.veggie.products.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.diegoparra.veggie.products.data.cart.CartDao
import com.diegoparra.veggie.products.data.cart.CartEntity
import com.diegoparra.veggie.products.data.products.room.*

@Database(
        entities = [
            TagEntity::class, MainEntity::class, VariationEntity::class,
            CartEntity::class
                   ],
        version = 1,
        exportSchema = false
)
@TypeConverters(ProductsConverters::class)
abstract class VeggieDatabase : RoomDatabase() {

    companion object {
        const val DB_NAME = "veggiedb"
    }

    abstract fun productsDao() : ProductsDao
    abstract fun cartDao() : CartDao

}