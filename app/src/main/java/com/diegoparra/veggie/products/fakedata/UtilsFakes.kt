package com.diegoparra.veggie.products.fakedata

import com.diegoparra.veggie.products.domain.entities.*

object UtilsFakes {

    private fun AdminProduct.getMainVariationInfo() =
        this.variations.find { it.varId == mainData.mainVariationId }
            ?: throw Exception("mainVariationId does not match any variation.")

    fun AdminProduct.toMainProduct() = MainProduct(
            mainData = mainData,
            mainVariationData = getMainVariationInfo()
    )

    fun AdminVariationData.toVariationProduct() = ProductVariation(
            variationData = this
    )

    fun AdminProduct.getIdTest(detail: String? = getMainVariationInfo().detailOptions?.get(0)) =
        ProductId(mainId = mainData.mainId,
            varId = getMainVariationInfo().varId,
            detail = detail
        )

}