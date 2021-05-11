package com.diegoparra.veggie.products.domain.entities

data class ProductCart(
        private val cartItem: CartItem,
        private val product: Product,
        val isEditable: Boolean = false
){
    val productId = cartItem.productId
    val name = product.name
    val imageUrl = product.imageUrl
    val unit = product.unit
    val weightGr = product.weightGr
    val price = product.price
    val discount = product.discount
    val stock = product.stock
    val maxOrder = product.maxOrder
    val label = product.label
    val detail = cartItem.productId.detail
    val quantity = cartItem.quantity
}