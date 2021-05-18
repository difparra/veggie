package com.diegoparra.veggie.products.cart.fakes

import com.diegoparra.veggie.products.cart.domain.ProductId
import com.diegoparra.veggie.products.domain.AdminProduct

object UtilsFakesCart {

    private fun AdminProduct.getMainVariationInfo() =
        this.variations.find { it.varId == mainData.mainVariationId }
            ?: throw Exception("mainVariationId does not match any variation.")

    fun AdminProduct.getIdTest(detail: String? = getMainVariationInfo().detailOptions?.get(0)) =
        ProductId(mainId = mainData.mainId,
            varId = getMainVariationInfo().varId,
            detail = detail
        )

}