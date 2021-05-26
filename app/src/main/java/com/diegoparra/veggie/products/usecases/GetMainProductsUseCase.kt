package com.diegoparra.veggie.products.usecases

import com.diegoparra.veggie.core.Either
import com.diegoparra.veggie.core.Failure
import com.diegoparra.veggie.core.mapListAndFlattenFailure
import com.diegoparra.veggie.core.map
import com.diegoparra.veggie.products.domain.Product
import com.diegoparra.veggie.products.entities.ProductMain
import com.diegoparra.veggie.products.cart.domain.CartRepository
import com.diegoparra.veggie.products.domain.ProductsRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import timber.log.Timber
import javax.inject.Inject

class GetMainProductsUseCase @Inject constructor(
    private val productsRepository: ProductsRepository,
    private val cartRepository: CartRepository
){

    /*
        NOTES ABOUT COROUTINES:
            CoroutineScope {}:  Used in suspend functions to get the coroutine scope,
            so that launch can be called inside suspend function

            launch {}:  Start a new coroutine, which mean that start a new job that will be
            executed concurrently (in parallel).
            launch can be called inside another coroutine, and job will be executed in parallel.
            This could be useful for getting info from individual products, they can be loaded
            concurrently.
            The parent coroutine will only complete until all its children are completed. In other words
            calling a suspend function will only complete and continue with the next line, until
            all the code inside the suspend function and internal suspend functions is complete.

            If new coroutines are launched inside another coroutine, but, for example, the second
            needs to wait until the first complete, they can be launched with async {} and then
            using .await(), that will return the value of the completed coroutine job.


            SUMMARY:

            launch{}: To call coroutines, jobs that work concurrently.
            launch{} inside launch{} (another coroutine):
                Will only complete and continue the main flow until all its children are completed.
                Execute children jobs concurrently.
                If needed some result in another job, async{} could be used instead, along with .await()
     */

    suspend operator fun invoke(params: Params) : Flow<Either<Failure, List<ProductMain>>> = coroutineScope {
        Timber.d("invoke() called with: params = $params")
        when(val products = getProducts(params)){
            is Either.Left -> flow { emit(products) }
            is Either.Right -> {
                val deferredList = mutableListOf<Deferred<Flow<Either<Failure, ProductMain>>>>()
                for(prod in products.b){
                    deferredList.add(
                            async {
                                addQuantityToProduct(prod)
                            }
                    )
                }
                val mainProdsQtyFlows = deferredList.awaitAll()
                combine(mainProdsQtyFlows){
                    it.toList().mapListAndFlattenFailure()
                }
            }
        }.flowOn(Dispatchers.IO)
    }

    /*suspend operator fun invoke(params: Params) : Flow<Either<Failure, List<ProductMain>>> {
        Timber.d("invoke() called with: params = $params")
        return when(val products = getProducts(params)){
            is Either.Left -> flow { emit(products) }
            is Either.Right -> {
                val mainProdsQtyFlows = products.b.map {
                    addQuantityToProduct(it)
                }
                combine(mainProdsQtyFlows){
                    it.toList().customTransformListToEither()
                }
            }
        }.flowOn(Dispatchers.IO)
    }*/

    private suspend fun getProducts(params: Params) : Either<Failure, List<Product>> {
        Timber.d("getProducts() called with: params = $params")
        return when(params){
            is Params.ForTag -> productsRepository.getMainProductsByTagId(params.tagId)
            is Params.ForSearch -> {
                if(params.nameQuery.isEmpty()){
                    Either.Left(Failure.SearchFailure.EmptyQuery)
                }else{
                    productsRepository.searchMainProductsByName(params.nameQuery)
                }
            }
        }
    }

    private fun addQuantityToProduct(mainProduct: Product) : Flow<Either<Failure, ProductMain>> {
        Timber.d("addQuantityToProduct() called with: product = $mainProduct")
        val quantity = cartRepository.getQuantityByMainId(mainProduct.mainId)
        return quantity.map { qtyEither ->
            qtyEither.map {
                ProductMain(mainProduct, it)
            }
        }
    }




    sealed class Params{
        class ForTag(val tagId: String) : Params()
        class ForSearch(val nameQuery: String) : Params()
    }


}