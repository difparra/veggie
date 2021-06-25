package com.diegoparra.veggie.order.usecases

import com.diegoparra.veggie.core.kotlin.*
import com.diegoparra.veggie.order.domain.ProductOrder
import com.diegoparra.veggie.order.domain.ProductsList
import com.diegoparra.veggie.products.cart.domain.CartItem
import com.diegoparra.veggie.products.cart.domain.CartRepository
import com.diegoparra.veggie.products.cart.domain.ProductId
import com.diegoparra.veggie.products.domain.Product
import com.diegoparra.veggie.products.domain.ProductsRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetProductsListOrder @Inject constructor(
    private val cartRepository: CartRepository,
    private val productsRepository: ProductsRepository
) {

    /**
     * Get the products to order in a list.
     * Should force database not to update. The products should be the same shown in the cart.
     */
    suspend operator fun invoke(): Either<Failure, ProductsList> {
        return getCartList()
            .suspendFlatMap { cartList ->
                cartList
                    .map { cartItem ->
                        getProductInfo(cartItem.productId).map { product ->
                            getProductOrder(
                                productId = cartItem.productId,
                                product = product,
                                cartItem = cartItem
                            )
                        }
                    }.reduceFailuresOrRight()
            }.map {
                ProductsList(it)
            }
    }

    private suspend fun getCartList(): Either<Failure, List<CartItem>> {
        return cartRepository.getAllCartItems().first()
    }

    private suspend fun getProductInfo(productId: ProductId): Either<Failure, Product> {
        /*
         *  forceUpdate need to be set to false. This is really important, do not update the local
         *  list, because order must show the same info that was shown in cart.
         */
        return productsRepository.getProduct(
            mainId = productId.mainId,
            varId = productId.varId,
            forceUpdate = false
        )
    }

    private fun getProductOrder(
        productId: ProductId,
        product: Product,
        cartItem: CartItem
    ): ProductOrder {
        return ProductOrder(
            productId = productId,
            name = product.mainData.name,
            unit = product.variationData.unit,
            weight = product.variationData.weightGr,
            price = product.variationData.price,
            discount = product.variationData.discount,
            quantity = cartItem.quantity
        )
    }


}