package com.diegoparra.veggie.products.data.products.firebase

import com.diegoparra.veggie.products.domain.entities.Product
import com.diegoparra.veggie.products.domain.entities.Tag
import com.diegoparra.veggie.products.domain.entities.Variation

object ProductDtosTransformations {

    fun TagDto.toTag() : Tag {
        return Tag(
                id = id,
                name = name
        )
    }

    fun ProductDto.toProduct() : Product {
        return Product(
                mainId = mainId,
                name = name,
                imageUrl = imageUrl,
                variation = variations.find { this.mainVarId == it.varId }?.toVariation(mainId)
                        ?: throw Exception("mainVarId does not match with any variation")
        )
    }

    private fun VariationDto.toVariation(mainId: String) : Variation {
        return Variation(
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

}