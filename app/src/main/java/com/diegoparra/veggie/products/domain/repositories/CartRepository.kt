package com.diegoparra.veggie.products.domain.repositories

import com.diegoparra.veggie.core.Either
import com.diegoparra.veggie.core.Failure
import com.diegoparra.veggie.products.domain.entities.CartItem
import com.diegoparra.veggie.products.domain.entities.ProductId
import kotlinx.coroutines.flow.Flow

interface CartRepository {

    fun getAllCartItems() : Flow<Either<Failure, List<CartItem>>>

    fun getQuantityItem(productId: ProductId) : Flow<Either<Failure, Int>>
    fun getQuantityByMainId(mainId: String) : Flow<Either<Failure, Int>>
    fun getQuantityMapByVariation(mainId: String, varId: String) : Flow<Either<Failure, Map<String?, Int>>>

    suspend fun addItem(cartItem: CartItem)
    suspend fun deleteItem(productId: ProductId)
    suspend fun updateQuantityItem(productId: ProductId, newQuantity: Int)

    suspend fun getItem(productId: ProductId) : Either<Failure, CartItem>
    suspend fun getCurrentQuantityItem(productId: ProductId) : Either<Failure, Int>

}