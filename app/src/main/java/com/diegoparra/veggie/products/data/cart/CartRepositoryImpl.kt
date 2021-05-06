package com.diegoparra.veggie.products.data.cart

import com.diegoparra.veggie.core.Either
import com.diegoparra.veggie.core.Failure
import com.diegoparra.veggie.products.data.cart.CartTransformations.toCartEntity
import com.diegoparra.veggie.products.data.cart.CartTransformations.toCartItem
import com.diegoparra.veggie.products.data.cart.CartTransformations.toMapQuantitiesByDetail
import com.diegoparra.veggie.products.data.cart.CartTransformations.toProdIdRoom
import com.diegoparra.veggie.products.domain.entities.CartItem
import com.diegoparra.veggie.products.domain.entities.ProductId
import com.diegoparra.veggie.products.domain.repositories.CartRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class CartRepositoryImpl (
        private val cartDao: CartDao,
        private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : CartRepository {

    override fun getAllCartItems(): Flow<Either<Failure, List<CartItem>>> {
        return cartDao.getAllCartItems().map {
            Either.Right(it.map { it.toCartItem() })
        }.flowOn(dispatcher)
    }

    override fun getQuantityByMainId(mainId: String): Flow<Either<Failure, Int>> {
        return cartDao.getQuantityByMainId(mainId).map {
            Either.Right(it ?: 0)
        }.flowOn(dispatcher)
    }

    override fun getQuantityItem(productId: ProductId): Flow<Either<Failure, Int>> {
        return cartDao.getQuantityItem(productId.toProdIdRoom()).map {
            Either.Right(it ?: 0)
        }.flowOn(dispatcher)
    }

    override fun getQuantityMapByVariation(mainId: String, varId: String): Flow<Either<Failure, Map<String?, Int>>> {
        return cartDao.getVariations(mainId, varId).map {
            Either.Right(it.toMapQuantitiesByDetail())
        }.flowOn(dispatcher)
    }

    override suspend fun addItem(cartItem: CartItem) = withContext(dispatcher) {
        cartDao.addItem(cartItem.toCartEntity())
    }

    override suspend fun deleteItem(productId: ProductId) = withContext(dispatcher) {
        cartDao.deleteItem(productId.toProdIdRoom())
    }

    override suspend fun updateQuantityItem(productId: ProductId, newQuantity: Int)  = withContext(dispatcher) {
        cartDao.updateQuantityItem(productId.toProdIdRoom(), newQuantity)
    }

    override suspend fun getItem(productId: ProductId): Either<Failure, CartItem>  = withContext(dispatcher) {
        val cartEntity = cartDao.getItem(productId.toProdIdRoom())
        return@withContext cartEntity?.let {
            Either.Right(it.toCartItem())
        } ?: Either.Left(Failure.ProductsFailure.ProductsNotFound)
    }

    override suspend fun getCurrentQuantityItem(productId: ProductId): Either<Failure, Int>  = withContext(dispatcher) {
        val quantity = cartDao.getCurrentQuantityItem(productId.toProdIdRoom())
        return@withContext Either.Right(quantity ?: 0)
    }
}