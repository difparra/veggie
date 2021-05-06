package com.diegoparra.veggie.products.domain.usecases

import com.diegoparra.veggie.core.Either
import com.diegoparra.veggie.core.Failure
import com.diegoparra.veggie.core.customTransformListToEither
import com.diegoparra.veggie.core.map
import com.diegoparra.veggie.products.domain.entities.MainProdWithQuantity
import com.diegoparra.veggie.products.domain.entities.MainProduct
import com.diegoparra.veggie.products.domain.repositories.CartRepository
import com.diegoparra.veggie.products.domain.repositories.ProductsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import timber.log.Timber
import javax.inject.Inject

class GetMainProductsUseCase @Inject constructor(
    private val productsRepository: ProductsRepository,
    private val cartRepository: CartRepository
){

    suspend operator fun invoke(params: Params) : Flow<Either<Failure, List<MainProdWithQuantity>>> {
        Timber.d("invoke() called with: params = $params")
        return when(val products = getProducts(params)){
            is Either.Left -> flow { emit(Either.Left(products.a)) }
            is Either.Right -> {
                val mainProdsQtyFlows = products.b.map {
                    addQuantityToProduct(it)
                }
                combine(mainProdsQtyFlows){
                    it.toList().customTransformListToEither()
                }
            }
        }.flowOn(Dispatchers.IO)
    }

    private suspend fun getProducts(params: Params) : Either<Failure, List<MainProduct>> {
        Timber.d("getProducts() called with: params = $params")
        return when(params){
            is Params.ForTag -> productsRepository.getMainProductsByTagId(params.tagId)
            is Params.ForSearch -> productsRepository.searchMainProductsByName(params.nameQuery)
        }
    }

    private fun addQuantityToProduct(product: MainProduct) : Flow<Either<Failure, MainProdWithQuantity>> {
        Timber.d("addQuantityToProduct() called with: product = $product")
        val quantity = cartRepository.getQuantityByMainId(product.mainId)
        return quantity.map { qtyEither ->
            qtyEither.map {
                MainProdWithQuantity(product, it)
            }
        }
    }




    sealed class Params{
        class ForTag(val tagId: String) : Params()
        class ForSearch(val nameQuery: String) : Params()
    }


}