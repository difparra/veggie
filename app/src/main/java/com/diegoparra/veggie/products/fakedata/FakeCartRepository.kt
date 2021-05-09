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


/*
    IMPORTANT NOTE:
        Flows are not working as flow.
        Flows are not being collected in useCase (just first time)

        Products and quantities are collected the first time, and can be shown on screen,
        but when add, update or delete quantity are called, they are executed but the flow
        collecting the values for the show quantities on screen is not working/collecting.

    **************
        Maybe is not that it is not that it is not working, maybe is just that the reference is the
        same so equals always returns true, so distinctUntilChanged() in the flow does not return and
        therefore does not emit anything.
        To solve this, it is necessary to copy the objects so that a new object with a new reference is created
*/



class FakeCartRepository (
    _cartItems: List<CartItem> = listOf(
        CartItem(productId = FakeProductsDatabase.banano_lbMV.getIdTest(detail = FakeDetail.Maduro.detail), quantity = 2),
        CartItem(productId = FakeProductsDatabase.banano_lbMV.getIdTest(detail = FakeDetail.Verde.detail), quantity = 5),
        CartItem(productId = FakeProductsDatabase.arandanos_bdj_lb.getIdTest(), quantity = 1),
        CartItem(productId = FakeProductsDatabase.granadilla_und.getIdTest(), quantity = 8)
    )
) : CartRepository {

    private val cartItems = MutableStateFlow(_cartItems)

    private suspend fun updateCartItems(newCartItems: List<CartItem>){
        cartItems.value = newCartItems
        //cartItems.emit(newCartItems)
    }

    override fun getAllCartItems(): Flow<Either<Failure, List<CartItem>>> {
        Timber.d("getAllCartItems() called")
        return cartItems.map {
            if(it.isNullOrEmpty()){
                Timber.e("getAllCartItems - Empty cart")
                Either.Left(Failure.ProductsFailure.ProductsNotFound)
            }else{
                Timber.i("getAllCartItems: $it")
                Either.Right(it)
            }
        }
    }

    override fun getQuantityItem(productId: ProductId): Flow<Either<Failure, Int>> {
        Timber.d("getQuantityItem() called with: productId = $productId")
        return cartItems.map {
            if(it.isNullOrEmpty()){
                Timber.e("getQuantityItem - Empty cart")
                return@map Either.Left(Failure.ProductsFailure.ProductsNotFound)
            }else{
                for(item in it){
                    if(item.productId == productId){
                        Timber.i("getQuantityItem: productId=$productId, quantity=${item.quantity}")
                        return@map Either.Right(item.quantity)
                    }
                }
                Timber.i("getQuantityItem: productId=$productId, quantity=0")
                return@map Either.Right(0)
            }
        }
    }

    override fun getQuantityByMainId(mainId: String): Flow<Either<Failure, Int>> {
        Timber.d( "getQuantityByMainId() called with: mainId = $mainId")
        return cartItems.map {
            if(it.isNullOrEmpty()){
                Timber.e("getQuantityByMainId - Empty cart")
                Either.Left(Failure.ProductsFailure.ProductsNotFound)
            }else{
                var qty = 0
                for(item in it){
                    if(item.productId.mainId == mainId){
                        qty += item.quantity
                    }
                }
                Timber.i("getQuantityByMainId: mainId=$mainId, qty=$qty")
                Either.Right(qty)
            }
        }
    }

    override fun getQuantityMapByVariation(mainId: String, varId: String): Flow<Either<Failure, Map<String?, Int>>> {
        Timber.d("getQuantitiesByDetailsVariation() called with: mainId = $mainId, varId = $varId")
        return cartItems.map {
            if(it.isNullOrEmpty()){
                Timber.e("getQuantititiesByDetailsVariation - Empty cart")
                return@map Either.Left(Failure.ProductsFailure.ProductsNotFound)
            }else{
                val quantitiesMap = mutableMapOf<String?, Int>()
                for(item in it){
                    if(item.productId.mainId == mainId && item.productId.varId == varId){
                        quantitiesMap[item.productId.detail] = item.quantity
                    }
                }
                Timber.i("getQuantitiesByDetailsVariation: quantitiesMap: $quantitiesMap")
                return@map Either.Right(quantitiesMap)
            }
        }
    }

    override suspend fun addItem(cartItem: CartItem) {
        Timber.d("addItem() called with: cartItem = $cartItem")
        val currentCartItems = cartItems.value.toMutableList()
        currentCartItems.add(cartItem)
        updateCartItems(currentCartItems.toList())
//        upsertCartItem(cartItem)
        Timber.i("addItem: productId=${cartItem.productId}, quantity=${cartItems.value.find { it.productId == cartItem.productId }?.quantity}")
    }

    override suspend fun deleteItem(productId: ProductId) {
        Timber.d("deleteItem() called with: productId = $productId")
        val currentCartItems = cartItems.value.toMutableList()
        currentCartItems.indexOfFirst { it.productId == productId }.let {
            if(it != -1)    currentCartItems.removeAt(it)
        }
        updateCartItems(currentCartItems)
        Timber.i("deleteItem: productId=$productId, prodInCart=${cartItems.value.find { it.productId == productId }}")
    }

    override suspend fun updateQuantityItem(productId: ProductId, newQuantity: Int) {
        Timber.d("updateQuantityItem() called with: productId = $productId, newQuantity = $newQuantity")
        val currentCartItems = cartItems.value.toMutableList()
        val index = currentCartItems.indexOfFirst { it.productId == productId }
        if(index != -1){
            currentCartItems[index] = currentCartItems[index].copy(quantity = newQuantity)
            updateCartItems(currentCartItems)
        }
        Timber.i("updateQuantityItem: productId=$productId, quantity=${cartItems.value.find { it.productId == productId }?.quantity}")
    }

    override suspend fun getItem(productId: ProductId): Either<Failure, CartItem> {
        Timber.d("getItem() called with: productId = $productId")
        val items = cartItems.value
        for(item in items){
            if(item.productId == productId){
                Timber.i("getItem: $item")
                return Either.Right(item)
            }
        }
        Timber.e("getItem: $productId not found")
        return Either.Left(Failure.ProductsFailure.ProductsNotFound)
    }

    override suspend fun getCurrentQuantityItem(productId: ProductId): Either<Failure, Int> {
        Timber.d("getCurrentQuantityItem() called with: productId = $productId")
        val items = cartItems.value
        for(item in items){
            if(item.productId == productId){
                Timber.i("getCurrentQuantityItem: productId=$productId, quantity=${item.quantity}")
                return Either.Right(item.quantity)
            }
        }
        Timber.i("getCurrentQuantityItem: productId=$productId, quantity=0")
        return Either.Right(0)
    }

    override fun getProdIdsList(): Flow<Either<Failure, List<ProductId>>> {
        return cartItems.map {
            Either.Right(it.map { it.productId })
        }
    }

}