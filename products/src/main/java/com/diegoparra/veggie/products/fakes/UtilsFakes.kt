package com.diegoparra.veggie.products.fakes

import com.diegoparra.veggie.products.domain.AdminProduct
import com.diegoparra.veggie.products.domain.AdminVariationData
import com.diegoparra.veggie.products.domain.Product
import com.diegoparra.veggie.products.domain.Variation

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

}