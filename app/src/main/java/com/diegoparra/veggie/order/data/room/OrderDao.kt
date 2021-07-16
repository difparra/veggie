package com.diegoparra.veggie.order.data.room

import androidx.room.*
import com.diegoparra.veggie.core.android.LocalUpdateHelper
import com.diegoparra.veggie.order.data.room.entities.OrderDetailsEntity
import com.diegoparra.veggie.order.data.room.entities.OrderUpdate
import com.diegoparra.veggie.order.data.room.entities.ProductOrderEntity
import com.diegoparra.veggie.order.data.room.entities.OrderEntity

@Dao
abstract class OrderDao: LocalUpdateHelper.RoomDb<OrderUpdate> {

    @Transaction
    @Query("Select * from OrderDetails where shipping_userId = :userId order by shipping_deliveryFromInMillis desc")
    abstract suspend fun getOrdersForUser(userId: String): List<OrderEntity>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract suspend fun upsertOrderDetails(orderDetails: OrderDetailsEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract suspend fun upsertProductsOrder(productsOrder: List<ProductOrderEntity>)

    /*
        Insert or update orders according to new data from server
     */
    @Transaction
    open override suspend fun updateItems(itemsUpdated: List<OrderUpdate>, itemsDeleted: List<String>) {
        if(itemsUpdated.isNullOrEmpty())  return
        itemsUpdated.forEach {
            upsertOrderDetails(it.orderDetails)
            upsertProductsOrder(it.productsOrder)
        }
    }

    @Query("Select MAX(updatedAtInMillis) from OrderDetails where shipping_userId = :userId")
    abstract override suspend fun getLastUpdatedTime(userId: String?): Long?

}