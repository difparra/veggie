package com.diegoparra.veggie

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.diegoparra.veggie.order.data.room.OrderConverters
import com.diegoparra.veggie.order.data.room.OrderDao
import com.diegoparra.veggie.order.data.room.entities.OrderDetailsEntity
import com.diegoparra.veggie.order.data.room.entities.ProductOrderEntity
import com.diegoparra.veggie.products.cart.data.room.CartDao
import com.diegoparra.veggie.products.cart.data.room.CartEntity
import com.diegoparra.veggie.products.data.room.*

@Database(
    entities = [
        TagEntity::class, MainEntity::class, VariationEntity::class,
        CartEntity::class,
        OrderDetailsEntity::class, ProductOrderEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(ProductConverters::class, OrderConverters::class)
abstract class VeggieDatabase : RoomDatabase() {

    companion object {
        const val DB_NAME = "veggiedb"
    }

    abstract fun productsDao(): ProductsDao
    abstract fun cartDao(): CartDao
    abstract fun orderDao(): OrderDao

}