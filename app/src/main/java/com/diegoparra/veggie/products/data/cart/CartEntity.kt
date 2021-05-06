package com.diegoparra.veggie.products.data.cart

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "Cart",
        indices = [
            Index(value = ["mainId"]),
            Index(value = ["varId"]),
            Index(value = ["detail"])
        ])
data class CartEntity(
        @PrimaryKey @Embedded val prodId: ProdIdRoom,
        val quantity: Int
)