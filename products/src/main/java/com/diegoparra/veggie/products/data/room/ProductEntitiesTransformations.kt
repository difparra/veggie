package com.diegoparra.veggie.products.data.room

import com.diegoparra.veggie.products.domain.*

object ProductEntitiesTransformations {

    fun TagEntity.toTag() = Tag(
            id = tagId,
            name = tagName
    )

    fun MainWithMainVariation.toProduct() = Product(
            tagId = mainEntity.relatedTagId,
            mainData = mainEntity.toMainData(),
            variationData = variationEntity.toVariationData(),
    )

    fun VariationWithMain.toProduct() = Product(
        tagId = mainEntity.relatedTagId,
        mainData = mainEntity.toMainData(),
        variationData = variationEntity.toVariationData(),
    )


    fun MainEntity.toMainData() = MainData(
        mainId = mainId,
        name = name,
        imageUrl = imageUrl,
        mainVariationId = mainVarId
    )

    fun VariationEntity.toVariationData() = VariationData(
            varId = varId,
            relatedMainId = relatedMainId,
            packet = packet,
            weight = weight,
            unit = unit,
            price = price,
            discount = discount,
            stock = stock,
            maxOrder = maxOrder,
            suggestedLabel = label,
            detailOptions = details
    )

}