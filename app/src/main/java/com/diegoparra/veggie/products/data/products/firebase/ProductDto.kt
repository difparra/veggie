package com.diegoparra.veggie.products.data.products.firebase

import com.google.firebase.Timestamp

data class ProductDto (
    val mainId: String,
    val tagId: String,
    val mainVarId: String,
    val name: String,
    val imageUrl: String,
    val updatedAt: Timestamp,
    val deleted: Boolean,
    val variations: List<VariationDto>          //  val variations: List<Map<String, Any>>
){
    //  Required empty constructor for firebase
    constructor() : this(
            mainId = "",
            tagId = "",
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
    val unit: String,
    val weightGr: Int,
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
            unit = "",
            weightGr = -1,
            price = -1,
            discount = 0f,
            stock = false,
            maxOrder = 0,
            label = null,
            details = null
    )
}