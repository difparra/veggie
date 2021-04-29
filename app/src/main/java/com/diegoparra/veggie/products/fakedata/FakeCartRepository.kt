package com.diegoparra.veggie.products.fakedata

import com.diegoparra.veggie.core.Either
import com.diegoparra.veggie.core.Failure
import com.diegoparra.veggie.products.domain.entities.CartItem
import com.diegoparra.veggie.products.domain.entities.ProductId
import com.diegoparra.veggie.products.domain.repositories.CartRepository
import com.diegoparra.veggie.products.fakedata.UtilsFakes.getIdTest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import timber.log.Timber

class FakeCartRepository (
    _cartItems: List<CartItem> = listOf(
        CartItem(productId = FakeProductsDatabase.banano_lbMV.getIdTest(detail = FakeProductsDatabase.FakeDetail.Maduro.detail), quantity = 2),
        CartItem(productId = FakeProductsDatabase.banano_lbMV.getIdTest(detail = FakeProductsDatabase.FakeDetail.Verde.detail), quantity = 5),
        CartItem(productId = FakeProductsDatabase.arandanos_bdj_lb.getIdTest(), quantity = 1),
        CartItem(productId = FakeProductsDatabase.granadilla_und.getIdTest(), quantity = 8)
    )
) : CartRepository {

    private val cartItems = MutableStateFlow(_cartItems)

    private suspend fun updateCartItems(newCartItems: List<CartItem>){
        cartItems.emit(newCartItems)
    }


    override fun getAllCartItems(): Flow<Either<Failure, List<CartItem>>> {
        return cartItems.map {
            if(it.isNullOrEmpty()){
                Timber.e("There are not products in the cart")
                Either.Left(Failure.ProductsFailure.ProductsNotFound)
            }else{
                Timber.i("Cart products: $it")
                Either.Right(it)
            }
        }
    }

    override fun getQuantityByMainId(mainId: String): Flow<Either<Failure, Int>> {
        return cartItems.map {
            if(it.isNullOrEmpty()){
                Timber.e("There are not products in the cart")
                Either.Left(Failure.ProductsFailure.ProductsNotFound)
            }else{
                var qty = 0
                for(item in it){
                    if(item.productId.mainId == mainId){
                        qty += item.quantity
                    }
                }
                Timber.i("Qty for $mainId = $qty")
                Either.Right(qty)
            }
        }
    }

    override fun getQuantityItem(productId: ProductId): Flow<Either<Failure, Int>> {
        return cartItems.map {
            if(it.isNullOrEmpty()){
                Timber.e("There are not products in the cart")
                return@map Either.Left(Failure.ProductsFailure.ProductsNotFound)
            }else{
                for(item in it){
                    if(item.productId == productId){
                        Timber.i("Qty for $productId = ${item.quantity}")
                        return@map Either.Right(item.quantity)
                    }
                }
                Timber.i("Qty for $productId = 0")
                return@map Either.Right(0)
            }
        }
    }

    override suspend fun addItem(cartItem: CartItem) {
        val currentCartItems = cartItems.value.toMutableSet()
        currentCartItems.add(cartItem)
        updateCartItems(currentCartItems.toList())
    }

    override suspend fun deleteItem(productId: ProductId) {
        val currentCartItems = cartItems.value.toMutableList()
        currentCartItems.indexOfFirst { it.productId == productId }.let {
            if(it != -1)    currentCartItems.removeAt(it)
        }
        updateCartItems(currentCartItems)
    }

    override suspend fun updateQuantityItem(productId: ProductId, newQuantity: Int) {
        val currentCartItems = cartItems.value.toMutableList()
        val index = currentCartItems.indexOfFirst { it.productId == productId }
        if(index != -1){
            currentCartItems[index] = currentCartItems[index].copy(quantity = newQuantity)
            updateCartItems(currentCartItems)
        }
    }

    override suspend fun getItem(productId: ProductId): Either<Failure, CartItem> {
        val items = cartItems.value
        for(item in items){
            if(item.productId == productId){
                Timber.i("item: $item")
                return Either.Right(item)
            }
        }
        Timber.e("item with productId: $productId not found")
        return Either.Left(Failure.ProductsFailure.ProductsNotFound)
    }

}