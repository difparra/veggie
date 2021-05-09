package com.diegoparra.veggie.products.domain.usecases

import com.diegoparra.veggie.core.Either
import com.diegoparra.veggie.core.Failure
import com.diegoparra.veggie.core.customTransformListToEither
import com.diegoparra.veggie.products.domain.entities.*
import com.diegoparra.veggie.products.domain.repositories.CartRepository
import com.diegoparra.veggie.products.domain.repositories.ProductsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetCartProductsUseCase @Inject constructor(
    private val productsRepository: ProductsRepository,
    private val cartRepository: CartRepository
) {

    //  TODO

    operator fun invoke() : Flow<Either<Failure, List<ProductCart>>> {
        val cartListFlow = getCartItems()
        return cartListFlow.map { cartListEither ->
            when(cartListEither){
                is Either.Left -> cartListEither
                is Either.Right -> {    // Must return a productCart list
                    val cartItemsList = cartListEither.b
                    val cartProdsList = cartItemsList.map { cartItem ->
                        when(val prodEither = productsRepository.getProduct(mainId = cartItem.productId.mainId, varId = cartItem.productId.varId)){
                            is Either.Left -> prodEither
                            is Either.Right -> {
                                val prodData = prodEither.b
                                Either.Right(
                                        ProductCart(
                                                cartItem = cartItem,
                                                product = prodData
                                        )
                                )
                            }
                        }
                    }.customTransformListToEither()
                    cartProdsList
                }
            }
        }
    }

    private fun getCartItems() : Flow<Either<Failure, List<CartItem>>> {
        return cartRepository.getAllCartItems()
    }

}