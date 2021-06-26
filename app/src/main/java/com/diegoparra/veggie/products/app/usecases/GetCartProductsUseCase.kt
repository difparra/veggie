package com.diegoparra.veggie.products.app.usecases

import com.diegoparra.veggie.core.kotlin.*
import com.diegoparra.veggie.products.app.entities.*
import com.diegoparra.veggie.products.cart.domain.CartRepository
import com.diegoparra.veggie.products.domain.ProductsRepository
import com.diegoparra.veggie.products.cart.domain.CartItem
import com.diegoparra.veggie.products.cart.domain.ProductId
import com.diegoparra.veggie.products.domain.Product
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class GetCartProductsUseCase @Inject constructor(
    private val productsRepository: ProductsRepository,
    private val cartRepository: CartRepository
) {

    /**
     * This use case must:
     *      Update all product info at the first time user see it.
     *      On the first collect, products that are no longer in stock should be deleted and correct
     *      prices should be fetched.
     *      Product info should not change while user is in the cart.
     */

    operator fun invoke(): Flow<Either<Failure, List<ProductCart>>> {
        return getProdsIdsCart().flatMapLatest { prodIdEither ->
            when (prodIdEither) {
                is Either.Left -> flow { emit(prodIdEither) }
                is Either.Right -> {
                    //  Check, otherwise, flatMap will return combine, and in there, an empty list
                    //  of flow will not emit anything, without letting the ui know that cart has
                    //  no products
                    if(prodIdEither.b.isEmpty()) {
                        return@flatMapLatest flow { emit(Either.Right(listOf<ProductCart>())) }
                    }

                    val prodsCartAsyncList =
                        prodIdEither.b.map { getProductCartAsync(it) }
                    val prodsCartFlows = prodsCartAsyncList.awaitAll()
                    combine(prodsCartFlows) {
                        it.toList().reduceFailuresOrRight()
                    }
                }
            }
        }.flowOn(Dispatchers.IO)
    }

    private suspend fun getProductCartAsync(id: ProductId) = coroutineScope {
        async {
            getProductInfo(id).fold(
                { flow { emit(Either.Left(it)) } },
                {
                    deleteFromCartIfNoStock(id, it)
                    getProductCart(id, it)
                }
            )
        }
    }



    private fun getProdsIdsCart(): Flow<Either<Failure, List<ProductId>>> {
        return cartRepository.getProdIdsList()
    }

    private suspend fun getProductInfo(productId: ProductId): Either<Failure, Product> {
        return productsRepository.getProduct(mainId = productId.mainId, varId = productId.varId)
    }

    private suspend fun deleteFromCartIfNoStock(id: ProductId, productInfo: Product) {
        if (!productInfo.variationData.stock) {
            cartRepository.deleteItem(id)
        }
    }

    private fun getProductCart(
        id: ProductId,
        productInfo: Product
    ): Flow<Either<Failure, ProductCart>> {
        val quantity = cartRepository.getQuantityItem(id)
        return quantity.map {
            it.map {
                ProductCart(
                    cartItem = CartItem(productId = id, quantity = it),
                    product = productInfo
                )
            }
        }
    }
}