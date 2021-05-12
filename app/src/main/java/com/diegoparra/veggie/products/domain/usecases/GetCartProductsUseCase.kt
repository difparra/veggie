package com.diegoparra.veggie.products.domain.usecases

import com.diegoparra.veggie.core.Either
import com.diegoparra.veggie.core.Failure
import com.diegoparra.veggie.core.customTransformListToEither
import com.diegoparra.veggie.core.map
import com.diegoparra.veggie.products.domain.entities.*
import com.diegoparra.veggie.products.domain.repositories.CartRepository
import com.diegoparra.veggie.products.domain.repositories.ProductsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class GetCartProductsUseCase @Inject constructor(
    private val productsRepository: ProductsRepository,
    private val cartRepository: CartRepository
) {

    /*  Option 2:       ** Already verified with the logs on productsRepository
            Get ids in the cartList.
            Possible changes in cartPage:
            -> updateQuantity without removing products from the cart:
                productsRepo will not be requested, product info will be kept the same and just the quantity changes.
            -> remove some product from the cart:
                as the size changes, the cart list itself changes, and will request all the information from the
                products database again.

            The advantage of using this way is also that there are less probabilities that the product
            info such as prices change while the client is modifying quantities in the cart.
            -> There is still some edge case when info is requested again in products database:
                When a product was removed from cart. But the request to the products database can also be managed as
                collect fresh info from online just if ___ minutes has been passed, so the info is outdated.
                (Setting my custom expirationTime)
    */

    operator fun invoke() : Flow<Either<Failure, List<ProductCart>>> {
        return getProdsIdsCart().flatMapLatest { prodIdEither ->
            when(prodIdEither){
                is Either.Left -> flow { emit(prodIdEither) }
                is Either.Right -> {
                    val flows = prodIdEither.b.map {
                        when(val prodInfo = getProductInfo(it)) {
                            is Either.Left -> flow { emit(prodInfo) }
                            is Either.Right -> addQuantityInfo(it, prodInfo.b)
                        }
                    }
                    combine(flows) {
                        it.toList().customTransformListToEither()
                    }
                }
            }
        }.flowOn(Dispatchers.IO)
    }

    private fun getProdsIdsCart() : Flow<Either<Failure, List<ProductId>>> {
        return cartRepository.getProdIdsList()
    }

    private suspend fun getProductInfo(productId: ProductId) : Either<Failure, Product> {
        //  TODO:   Define custom expiration time for updating product info in cart.
        val prod =productsRepository.getProduct(mainId = productId.mainId, varId = productId.varId)
        //  TODO:   This has not still been tested if is working
        //  Check if product has no longer stock, and if so, delete from cart.
        //      This can occur when the user had a saved cart list from the previous day, and the
        //      next day, some items in the cart list would possibly have no longer stock.
        if(prod is Either.Right && !prod.b.stock){
            cartRepository.deleteItem(productId = productId)
        }
        return prod
    }

    private fun addQuantityInfo(productId: ProductId, product: Product) : Flow<Either<Failure, ProductCart>> {
        val quantity = cartRepository.getQuantityItem(productId)
        return quantity.map {
            it.map {
                ProductCart(
                    cartItem = CartItem(productId = productId, quantity = it),
                    product = product
                )
            }
        }
    }

}

/*
    //  Option 1:
    //      This way is working, but whenever a quantity is changed, cartListFlow is changed and therefore
    //      all the info for all the products in the cartList is requested.
    //      Verified with the logs on FakeProductsRepository

    operator fun invoke() : Flow<Either<Failure, List<ProductCart>>> {
        val cartListFlow = getCartItems()
        return cartListFlow.map { cartListEither ->
            when(cartListEither){
                is Either.Left -> cartListEither
                is Either.Right -> {    // Must return a productCart list
                    cartListEither.b
                        .map {
                            addProductInfo(it)
                        }
                        .customTransformListToEither()
                }
            }
        }
    }

    private fun getCartItems() : Flow<Either<Failure, List<CartItem>>> {
        return cartRepository.getAllCartItems()
    }

    private suspend fun addProductInfo(item: CartItem) : Either<Failure, ProductCart> {
        return when(val prodEither = productsRepository.getProduct(mainId = item.productId.mainId, varId = item.productId.varId)){
            is Either.Left -> prodEither
            is Either.Right -> Either.Right(
                ProductCart(
                    cartItem = item,
                    product = prodEither.b
                )
            )
        }
    }
 */