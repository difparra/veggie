package com.diegoparra.veggie.products.app.usecases

import com.diegoparra.veggie.core.kotlin.*
import com.diegoparra.veggie.products.cart.domain.ProductId
import com.diegoparra.veggie.products.app.entities.ProductVariation
import com.diegoparra.veggie.products.cart.domain.CartRepository
import com.diegoparra.veggie.products.domain.ProductsRepository
import com.diegoparra.veggie.products.domain.VariationData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class GetVariationsUseCase @Inject constructor(
    private val productsRepository: ProductsRepository,
    private val cartRepository: CartRepository
) {

    suspend operator fun invoke(
        mainId: String,
        isInternetAvailable: Boolean
    ): Flow<Either<Failure, List<ProductVariation>>> {
        return when (val variations = getVariations(mainId, isInternetAvailable)) {
            is Either.Left -> flow { emit(variations) }
            is Either.Right -> {
                //  Check if variationsList is empty before returning combine.
                if (variations.b.isEmpty()) {
                    return flow { emit(Either.Right(listOf())) }
                }

                val variationsFlows = variations.b.map {
                    getProductVariation(mainId = mainId, variation = it)
                }
                combine(variationsFlows) {
                    it.toList().reduceFailuresOrRight()
                }
            }
        }.flowOn(Dispatchers.IO)
    }

    private suspend fun getVariations(
        mainId: String,
        isInternetAvailable: Boolean
    ): Either<Failure, List<VariationData>> {
        val source = ProductsRepository.getDefaultSourceForInternetAccessState(isInternetAvailable)
        return productsRepository.getVariationsByMainId(mainId = mainId, source = source)
    }

    private fun getProductVariation(
        mainId: String,
        variation: VariationData
    ): Flow<Either<Failure, ProductVariation>> {
        return if (variation.hasDetails()) {
            val qtyMap =
                cartRepository.getQuantityMapByVariation(mainId = mainId, varId = variation.varId)
            qtyMap.map { qtyEither ->
                qtyEither.map {
                    ProductVariation(variation = variation, quantitiesByDetail = it)
                }
            }
        } else {
            val quantity =
                cartRepository.getQuantityItem(
                    ProductId(
                        mainId = mainId,
                        varId = variation.varId,
                        detail = null
                    )
                )
            quantity.map { qtyEither ->
                qtyEither.map {
                    ProductVariation(variation = variation, quantity = it)
                }
            }
        }
    }
}