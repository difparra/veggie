package com.diegoparra.veggie.products.fakedata

import com.diegoparra.veggie.products.domain.entities.*

object UtilsFakes {

    fun AdminProduct.toProduct(varId: String) : Product {
        val variation = this.variations.find { it.varId == varId }
        return variation?.let {
            Product(
                    mainId = mainData.mainId,
                    name = mainData.name,
                    imageUrl = mainData.imageUrl,
                    variation = it.toVariation()
            )
        } ?: throw Throwable("varId: $varId not found for mainId: ${mainData.mainId}")
    }

    fun AdminVariationData.toVariation() : Variation {
        return Variation(
                mainId = relatedMainId,
                varId = varId,
                unit = unit,
                weightGr = weightGr,
                details = detailOptions,
                price = price,
                discount = discount,
                stock = stock,
                maxOrder = maxOrder,
                suggestedLabel = suggestedLabel
        )
    }



    private fun AdminProduct.getMainVariationInfo() =
        this.variations.find { it.varId == mainData.mainVariationId }
            ?: throw Exception("mainVariationId does not match any variation.")
    
    fun AdminProduct.getIdTest(detail: String? = getMainVariationInfo().detailOptions?.get(0)) =
        ProductId(mainId = mainData.mainId,
            varId = getMainVariationInfo().varId,
            detail = detail
        )

}