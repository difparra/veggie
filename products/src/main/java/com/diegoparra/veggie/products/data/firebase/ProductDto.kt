package com.diegoparra.veggie.products.data.firebase

import com.google.firebase.Timestamp

data class ProductDto (
    val tagId: String,
    val mainId: String,
    val mainVarId: String,
    val name: String,
    val imageUrl: String,
    val updatedAt: Timestamp,
    val deleted: Boolean,
    val variations: List<VariationDto>          //  val variations: List<Map<String, Any>>
){
    //  Required empty constructor for firebase
    constructor() : this(
            tagId = "",
            mainId = "",
            mainVarId = "",
            name = "",
            imageUrl = "",
            updatedAt = Timestamp(0,0),
            deleted = false,
            variations = listOf()
    )
}

data class VariationDto (
    val varId: String,
    val packet: String,
    val weight: Int,
    val unit: String,
    val price: Int,
    val discount: Float,
    val stock: Boolean,
    val maxOrder: Int,
    val label: String?,
    val details: List<String>?
){
    //  Required empty constructor for firebase
    constructor() : this(
            varId = "",
            packet = "",
            weight = -1,
            unit = "",
            price = -1,
            discount = 0f,
            stock = false,
            maxOrder = 0,
            label = null,
            details = null
    )
}