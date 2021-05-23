package com.diegoparra.veggie.products.data.firebase

import com.diegoparra.veggie.products.domain.Product
import com.diegoparra.veggie.products.domain.Tag
import com.diegoparra.veggie.products.domain.Variation


object ProductDtoTransformations {

    fun TagDto.toTag() = Tag(
            id = id,
            name = name
    )

    fun ProductDto.toProduct() = Product(
            mainId = mainId,
            name = name,
            imageUrl = imageUrl,
            variation = variations.find { this.mainVarId == it.varId }?.toVariation(mainId)
                    ?: throw Exception("mainVarId does not match with any variation")
    )

    private fun VariationDto.toVariation(mainId: String) = Variation(
            mainId = mainId,
            varId = varId,
            unit = unit,
            weightGr = weightGr,
            details = details,
            price = price,
            discount = discount/100,
            stock = stock,
            maxOrder = maxOrder,
            suggestedLabel = label
    )

}