package com.diegoparra.veggie.products.domain.usecases

import com.diegoparra.veggie.core.Either
import com.diegoparra.veggie.core.Failure
import com.diegoparra.veggie.core.customTransformListToEither
import com.diegoparra.veggie.core.map
import com.diegoparra.veggie.products.domain.entities.ProdVariationWithQuantities
import com.diegoparra.veggie.products.domain.entities.ProductId
import com.diegoparra.veggie.products.domain.entities.ProductVariation
import com.diegoparra.veggie.products.domain.repositories.CartRepository
import com.diegoparra.veggie.products.domain.repositories.ProductsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import timber.log.Timber
import javax.inject.Inject

class GetVariationsUseCase @Inject constructor(
        private val productsRepository: ProductsRepository,
        private val cartRepository: CartRepository
) {

    suspend operator fun invoke(mainId: String) : Flow<Either<Failure, List<ProdVariationWithQuantities>>> {
        Timber.d("invoke() called with: mainId = $mainId")
        return when(val variations = getVariations(mainId)){
            is Either.Left -> flow { emit(variations) }
            is Either.Right -> {
                val varsQtyFlows = variations.b.map {
                    addQuantityToVariation(mainId = mainId, variation = it)
                }
                combine(varsQtyFlows){
                    Timber.d("invoke - combine called. ${it.toList()}")
                    it.toList().customTransformListToEither()
                }
            }
        }.flowOn(Dispatchers.IO)
    }

    private suspend fun getVariations(mainId: String) : Either<Failure, List<ProductVariation>> {
        Timber.d("getVariations() called with: mainId = $mainId")
        return productsRepository.getProductVariationsByMainId(mainId = mainId)
    }


    private fun addQuantityToVariation(mainId: String, variation: ProductVariation) : Flow<Either<Failure, ProdVariationWithQuantities>> {
        Timber.d("addQuantityToVariation() called with: mainId = $mainId, variation = $variation")
        return if(variation.details.isNullOrEmpty()){
            val quantity = cartRepository.getQuantityItem(ProductId(mainId = mainId, varId = variation.varId, detail = null))
            quantity.map { qtyEither ->
                qtyEither.map {
                    ProdVariationWithQuantities(variation, it)
                }
            }
        }else{
            val detailsWithQuantitiesMap =
                    cartRepository.getQuantityMapByVariation(mainId = mainId, varId = variation.varId)
            detailsWithQuantitiesMap.map { qtyEither ->
                qtyEither.map {
                    ProdVariationWithQuantities(variation, it)
                }
            }
        }
    }
}