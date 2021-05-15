package com.diegoparra.veggie.products.room

import androidx.room.Embedded
import androidx.room.Relation


data class MainWithMainVariation(
    @Embedded
        val mainEntity: MainEntity,
    @Relation(parentColumn = "mainVarId", entityColumn = "varId")
        val variationEntity: VariationEntity
)

data class VariationWithMain(
    @Embedded
        val variationEntity: VariationEntity,
    @Relation(parentColumn = "relatedMainId", entityColumn = "mainId")
        val mainEntity: MainEntity
)

data class ProductUpdateRoom(
    val mainEntity: MainEntity,
    val variations: List<VariationEntity>
)