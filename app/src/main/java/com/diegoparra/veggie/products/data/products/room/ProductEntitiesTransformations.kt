package com.diegoparra.veggie.products.data.products.room

import com.diegoparra.veggie.products.domain.entities.Product
import com.diegoparra.veggie.products.domain.entities.Tag
import com.diegoparra.veggie.products.domain.entities.Variation

object ProductEntitiesTransformations {

    fun TagEntity.toTag() = Tag(
            id = tagId,
            name = tagName
    )

    fun MainWithMainVariation.toProduct() = Product(
            mainId = mainEntity.mainId,
            name = mainEntity.name,
            imageUrl = mainEntity.imageUrl,
            variation = variationEntity.toVariation()
    )

    fun VariationEntity.toVariation() = Variation(
            mainId = relatedMainId,
            varId = varId,
            unit = unit,
            weightGr = weightGr,
            details = details,
            price = price,
            discount = discount,
            stock = stock,
            maxOrder = maxOrder,
            suggestedLabel = label
    )

    fun VariationWithMain.toProduct() = Product(
            mainId = mainEntity.mainId,
            name = mainEntity.name,
            imageUrl = mainEntity.imageUrl,
            variation = variationEntity.toVariation()
    )

}