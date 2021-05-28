package com.diegoparra.veggie.products.usecases

import com.diegoparra.veggie.core.Either
import com.diegoparra.veggie.core.Failure
import com.diegoparra.veggie.core.mapListAndFlattenFailure
import com.diegoparra.veggie.core.map
import com.diegoparra.veggie.products.cart.domain.ProductId
import com.diegoparra.veggie.products.entities.ProductVariation
import com.diegoparra.veggie.products.cart.domain.CartRepository
import com.diegoparra.veggie.products.domain.ProductsRepository
import com.diegoparra.veggie.products.domain.VariationData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import timber.log.Timber
import javax.inject.Inject

class GetVariationsUseCase @Inject constructor(
    private val productsRepository: ProductsRepository,
    private val cartRepository: CartRepository
) {

    suspend operator fun invoke(mainId: String) : Flow<Either<Failure, List<ProductVariation>>> {
        Timber.d("invoke() called with: mainId = $mainId")
        return when(val variations = getVariations(mainId)){
            is Either.Left -> flow { emit(variations) }
            is Either.Right -> {
                val varsQtyFlows = variations.b.map {
                    addQuantityToVariation(mainId = mainId, variation = it)
                }
                combine(varsQtyFlows){
                    Timber.d("invoke - combine called. ${it.toList()}")
                    it.toList().mapListAndFlattenFailure()
                }
            }
        }.flowOn(Dispatchers.IO)
    }

    private suspend fun getVariations(mainId: String) : Either<Failure, List<VariationData>> {
        Timber.d("getVariations() called with: mainId = $mainId")
        return productsRepository.getVariationsByMainId(mainId = mainId)
    }


    private fun addQuantityToVariation(mainId: String, variation: VariationData) : Flow<Either<Failure, ProductVariation>> {
        Timber.d("addQuantityToVariation() called with: mainId = $mainId, variation = $variation")
        return if(variation.hasDetails()) {
            val qtyMap =
                    cartRepository.getQuantityMapByVariation(mainId = mainId, varId = variation.varId)
            qtyMap.map { qtyEither ->
                qtyEither.map {
                    ProductVariation(variation = variation, quantitiesByDetail = it)
                }
            }
        }else{
            val quantity =
                    cartRepository.getQuantityItem(ProductId(mainId = mainId, varId = variation.varId, detail = null))
            quantity.map { qtyEither ->
                qtyEither.map {
                    ProductVariation(variation = variation, quantity = it)
                }
            }
        }
    }
}