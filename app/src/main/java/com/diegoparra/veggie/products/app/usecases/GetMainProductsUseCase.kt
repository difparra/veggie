package com.diegoparra.veggie.products.app.usecases

import com.diegoparra.veggie.core.kotlin.*
import com.diegoparra.veggie.products.app.entities.ProductMain
import com.diegoparra.veggie.products.cart.domain.CartRepository
import com.diegoparra.veggie.products.domain.Product
import com.diegoparra.veggie.products.domain.ProductsRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import timber.log.Timber
import javax.inject.Inject

class GetMainProductsUseCase @Inject constructor(
    private val productsRepository: ProductsRepository,
    private val cartRepository: CartRepository
) {

    suspend operator fun invoke(
        params: Params,
        isInternetAvailable: Boolean
    ): Flow<Either<Failure, List<ProductMain>>> {
        return getProducts(params, isInternetAvailable).fold(
            {
                Timber.d("productsList returned failure: $it")
                createSingleFlowEither(failure = it)
            },
            {
                if (it.isEmpty()) {
                    Timber.d("productsList is empty")
                    return createSingleFlowEither(success = listOf())
                }

                //  Be careful when calling combine(List<flows>) because if the list is empty,
                //  combine will return a flow that does nothing, that will never emit,
                //  will not even emit an empty list, and if it is like app crashes or freezes.
                val deferredList = it.map { getProductMainAsync(it) }
                val mainProdsFlows = deferredList.awaitAll()
                Timber.d("Collected products as flow mainProdsFlows=$mainProdsFlows")
                combine(mainProdsFlows) {
                    Timber.d("Calling combine")
                    it.toList().reduceFailuresOrRight()
                }
            }
        ).flowOn(Dispatchers.IO)
    }

    private suspend fun getProducts(
        params: Params,
        isInternetAvailable: Boolean
    ): Either<Failure, List<Product>> {
        val source = ProductsRepository.getDefaultSourceForInternetAccessState(isInternetAvailable)
        return when (params) {
            is Params.ForTag -> productsRepository.getMainProductsByTagId(params.tagId, source)
            is Params.ForSearch -> {
                if (params.nameQuery.isEmpty()) {
                    Timber.d("nameQuery is empty")
                    Either.Right(listOf())
                } else {
                    Timber.d("nameQuery = ${params.nameQuery}")
                    productsRepository.searchMainProductsByName(params.nameQuery, source)
                }
            }
        }
    }

    private fun getProductMain(product: Product): Flow<Either<Failure, ProductMain>> {
        val quantity = cartRepository.getQuantityByMainId(product.mainData.mainId)
        return quantity.map { qtyEither ->
            qtyEither.map {
                ProductMain(product, it)
            }
        }
    }

    private suspend fun getProductMainAsync(product: Product) = coroutineScope {
        async { getProductMain(product) }
    }


    sealed class Params {
        class ForTag(val tagId: String) : Params()
        class ForSearch(val nameQuery: String) : Params()
    }

}