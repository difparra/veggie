package com.diegoparra.veggie.products.domain.entities

/*
    Products that will be used in the viewModel / ui layer
 */

data class MainProdWithQuantity(
    val mainProduct: MainProduct,
    val quantity: Int
){
    val mainId = mainProduct.mainId
    val name = mainProduct.name
    val imageUrl = mainProduct.imageUrl
    val unit = mainProduct.unit
    val weightGr = mainProduct.weightGr
    val price = mainProduct.price
    val discount = mainProduct.discount
    val stock = mainProduct.stock
    val suggestedLabel = mainProduct.suggestedLabel
}

data class VarProdWithQuantity(
    val variationProduct: VariationProduct,
    val quantity: Int
){
    val varId = variationProduct.varId
    val unit = variationProduct.unit
    val weightGr = variationProduct.weightGr
    val details = variationProduct.details
    val price = variationProduct.price
    val discount = variationProduct.discount
    val stock = variationProduct.stock
    val maxOrder = variationProduct.maxOrder
    val suggestedLabel = variationProduct.suggestedLabel
}