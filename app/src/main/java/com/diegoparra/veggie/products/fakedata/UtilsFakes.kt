package com.diegoparra.veggie.products.fakedata

import com.diegoparra.veggie.products.domain.entities.*

object UtilsFakes {

    private fun ProductAdmin.getMainVariationInfo() =
        this.variations.find { it.varId == mainData.mainVariationId }
            ?: throw Exception("mainVariationId does not match any variation.")

    fun ProductAdmin.toMainProduct() = MainProduct(
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

    fun VariationDataAdmin.toVariationProduct() = VariationProduct(
        varId = varId, unit = unit, weightGr = weightGr, details = detailOptions,
        price = price, discount = discount, stock = stock, maxOrder = maxOrder,
        suggestedLabel = suggestedLabel
    )

    fun ProductAdmin.getIdTest(detail: String = getMainVariationInfo().detailOptions[0]) =
        ProductId(mainId = mainData.mainId,
            varId = getMainVariationInfo().varId,
            detail = detail
        )

}