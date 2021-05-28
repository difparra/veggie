package com.diegoparra.veggie.products.fakes

import com.diegoparra.veggie.products.domain.Product
import com.diegoparra.veggie.products.domain.ProductWithAllVariations

object UtilsFakes {

    fun ProductWithAllVariations.toProduct(varId: String) : Product {
        val variation = this.variations.find { it.varId == varId }
        return variation?.let {
            Product(
                tagId = tagId,
                mainData = mainData,
                variationData = it
            )
        } ?: throw Throwable("varId: $varId not found for mainId: ${mainData.mainId}")
    }

}