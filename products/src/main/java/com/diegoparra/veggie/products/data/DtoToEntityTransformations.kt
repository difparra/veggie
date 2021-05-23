package com.diegoparra.veggie.products.data

import com.diegoparra.veggie.core.removeCaseAndAccents
import com.diegoparra.veggie.products.data.firebase.ProductDto
import com.diegoparra.veggie.products.data.firebase.TagDto
import com.diegoparra.veggie.products.data.firebase.VariationDto
import com.diegoparra.veggie.products.data.room.MainEntity
import com.diegoparra.veggie.products.data.room.ProductUpdateRoom
import com.diegoparra.veggie.products.data.room.TagEntity
import com.diegoparra.veggie.products.data.room.VariationEntity
import com.google.firebase.Timestamp

object DtoToEntityTransformations {

    fun TagDto.toTagEntity() = TagEntity(
            tagId = id,
            tagName = name
    )


    fun Timestamp.toMillis() : Long = seconds * 1000
    fun Long.toTimestamp() = Timestamp(this/1000, 0)

    fun List<ProductDto>.getMainProdIdsToDelete() : List<String> {
        return this.filter { it.deleted }.map { it.mainId }
    }

    fun List<ProductDto>.getListProdUpdateRoom() : List<ProductUpdateRoom> {
        return this.filterNot { it.deleted }.map { prodDto ->
            ProductUpdateRoom(
                    mainEntity = prodDto.toMainEntity(),
                    variations = prodDto.variations.map { it.toVariationEntity(prodDto.mainId) }
            )
        }
    }

    private fun ProductDto.toMainEntity() = MainEntity(
            mainId = mainId,
            relatedTagId = tagId,
            mainVarId = mainVarId,
            name = name,
            normalised_name = name.removeCaseAndAccents(),
            imageUrl = imageUrl,
            updatedAtInMillis = updatedAt.toMillis()
    )

    private fun VariationDto.toVariationEntity(mainId: String) = VariationEntity(
            varId = varId,
            relatedMainId = mainId,
            unit = unit,
            weightGr = weightGr,
            price = price,
            discount = discount/100,
            stock = stock,
            maxOrder = maxOrder,
            label = label,
            details = details
    )

}