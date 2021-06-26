package com.diegoparra.veggie.products.app.usecases

import com.diegoparra.veggie.core.kotlin.Either
import com.diegoparra.veggie.core.kotlin.Failure
import com.diegoparra.veggie.core.kotlin.reduceFailuresOrRight
import com.diegoparra.veggie.core.kotlin.map
import com.diegoparra.veggie.products.app.entities.ProductMain
import com.diegoparra.veggie.products.cart.domain.CartRepository
import com.diegoparra.veggie.products.domain.Product
import com.diegoparra.veggie.products.domain.ProductsRepository
import com.diegoparra.veggie.products.utils.ProductsFailure
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import timber.log.Timber
import javax.inject.Inject

class GetMainProductsUseCase @Inject constructor(
    private val productsRepository: ProductsRepository,
    private val cartRepository: CartRepository
) {

    suspend operator fun invoke(params: Params): Flow<Either<Failure, List<ProductMain>>> {
        return when (val products = getProducts(params)) {
            is Either.Left -> {
                flow { emit(products) }
            }
            is Either.Right -> {
                //  TODO:   Still check if dealing with empty list here is the best way, or if it is
                //          better to return a failure from the repository if the list is empty.
                //          A failure such as 404 not found when the list is empty.
                //  If list is empty, send a flow that EMIT an empty list, sth so that ui can work with it
                if(products.b.isEmpty()) {
                    return flow { emit(Either.Right(listOf())) }
                }

                val deferredList = products.b.map {
                    getProductMainAsync(it)
                }
                val mainProdsFlows = deferredList.awaitAll()
                /*
                    Be careful when calling combine(List<flows>) because if the list is empty,
                    combine will return a flow that does nothing, that will never emit,
                    will not even emit an empty list, and if it is like app crashes or freezes.
                 */
                combine(mainProdsFlows) {
                    it.toList().reduceFailuresOrRight()
                }
            }
        }.flowOn(Dispatchers.IO)
    }

    private suspend fun getProductMainAsync(product: Product) = coroutineScope {
        async { getProductMain(product) }
    }

    private suspend fun getProducts(params: Params): Either<Failure, List<Product>> {
        Timber.d("getProducts called")
        return when (params) {
            is Params.ForTag -> productsRepository.getMainProductsByTagId(params.tagId)
            is Params.ForSearch -> {
                if (params.nameQuery.isEmpty()) {
                    Timber.d("nameQuery is empty")
                    Either.Left(ProductsFailure.EmptySearchQuery)
                } else {
                    Timber.d("nameQuery = ${params.nameQuery}")
                    productsRepository.searchMainProductsByName(params.nameQuery)
                }
            }
        }
    }

    private fun getProductMain(product: Product): Flow<Either<Failure, ProductMain>> {
        Timber.d("getProductMain called")
        val quantity = cartRepository.getQuantityByMainId(product.mainData.mainId)
        return quantity.map { qtyEither ->
            qtyEither.map {
                ProductMain(product, it)
            }
        }
    }


    sealed class Params {
        class ForTag(val tagId: String) : Params()
        class ForSearch(val nameQuery: String) : Params()
    }

}