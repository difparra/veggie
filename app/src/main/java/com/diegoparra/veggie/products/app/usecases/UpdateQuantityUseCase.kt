package com.diegoparra.veggie.products.app.usecases

import com.diegoparra.veggie.core.kotlin.Either
import com.diegoparra.veggie.core.kotlin.Failure
import com.diegoparra.veggie.products.cart.domain.CartItem
import com.diegoparra.veggie.products.cart.domain.ProductId
import com.diegoparra.veggie.products.cart.domain.CartRepository
import timber.log.Timber
import javax.inject.Inject

class UpdateQuantityUseCase @Inject constructor(
    private val cartRepository: CartRepository
) {

    suspend operator fun invoke(params: Params) {
        when (params) {
            is Params.Add -> addQuantity(productId = params.productId, maxOrder = params.maxOrder)
            is Params.Reduce -> reduceQuantity(productId = params.productId)
        }
    }

    private suspend fun addQuantity(productId: ProductId, maxOrder: Int) {
        val currentQtyEither = getCurrentQuantity(productId)
        if (currentQtyEither is Either.Right) {
            val currQty = currentQtyEither.b
            if (currQty == 0) {
                cartRepository.addItem(CartItem(productId = productId, quantity = 1))
            } else if (currQty < maxOrder) {
                cartRepository.updateQuantityItem(productId = productId, newQuantity = currQty + 1)
            }
            // Here can be optionally added a new variable to toast a message indicating the user
            // that he can't order more items on this product
        } else {
            Timber.wtf("Couldn't find current quantity for product $productId")
        }
    }

    private suspend fun reduceQuantity(productId: ProductId) {
        val currentQtyEither = getCurrentQuantity(productId)
        if (currentQtyEither is Either.Right) {
            val currQty = currentQtyEither.b
            if (currQty > 1) {
                cartRepository.updateQuantityItem(productId = productId, newQuantity = currQty - 1)
            } else if (currQty == 1) {
                cartRepository.deleteItem(productId = productId)
            }
        }
    }


    private suspend fun getCurrentQuantity(productId: ProductId): Either<Failure, Int> {
        return cartRepository.getCurrentQuantityItem(productId)
    }

    sealed class Params {
        class Add(val productId: ProductId, val maxOrder: Int) : Params()
        class Reduce(val productId: ProductId) : Params()
    }

}