package com.diegoparra.veggie.products.fakedata

import com.diegoparra.veggie.products.domain.entities.*

object UtilsFakes {

    private fun AdminProduct.getMainVariationInfo() =
        this.variations.find { it.varId == mainData.mainVariationId }
            ?: throw Exception("mainVariationId does not match any variation.")

    fun AdminProduct.toMainProduct() = MainProduct(
        mainId = mainData.mainId,
        name = mainData.name,
        imageUrl = mainData.imageUrl,
        unit = getMainVariationInfo().unit,
        weightGr = getMainVariationInfo().weightGr,
        price = getMainVariationInfo().price,
        discount = getMainVariationInfo().discount,
        stock = getMainVariationInfo().stock,
        suggestedLabel = getMainVariationInfo().suggestedLabel
    )

    fun AdminVariationData.toVariationProduct() = ProductVariation(
        varId = varId, unit = unit, weightGr = weightGr, details = detailOptions,
        price = price, discount = discount, stock = stock, maxOrder = maxOrder,
        suggestedLabel = suggestedLabel
    )

    fun AdminProduct.getIdTest(detail: String? = getMainVariationInfo().detailOptions?.get(0)) =
        ProductId(mainId = mainData.mainId,
            varId = getMainVariationInfo().varId,
            detail = detail
        )

}