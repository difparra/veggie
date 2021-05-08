package com.diegoparra.veggie.products.domain.entities

data class ProductCart(
        private val cartItem: CartItem,
        private val mainData: AdminMainData,
        private val variationData: AdminVariationData
){
    val name = mainData.name
    val imageUrl = mainData.imageUrl
    val unit = variationData.unit
    val weightGr = variationData.weightGr
    val price = variationData.price
    val discount = variationData.discount
    val stock = variationData.stock
    val label = Label.createLabel(stock, discount, variationData.suggestedLabel)
    val detail = cartItem.productId.detail
    val quantity = cartItem.quantity
}