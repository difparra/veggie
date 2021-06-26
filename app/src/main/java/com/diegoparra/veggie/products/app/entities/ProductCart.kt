package com.diegoparra.veggie.products.app.entities

import com.diegoparra.veggie.products.cart.domain.CartItem
import com.diegoparra.veggie.products.domain.Product

data class ProductCart(
    private val cartItem: CartItem,
    private val product: Product,
    val isEditable: Boolean = false
) {
    val productId = cartItem.productId
    val detail = cartItem.productId.detail
    val quantity = cartItem.quantity

    private val mainData = product.mainData
    val name = mainData.name
    val imageUrl = mainData.imageUrl

    private val variationData = product.variationData
    val packet = variationData.packet
    val weight = variationData.weight
    val unit = variationData.unit
    val price = variationData.price
    val discount = variationData.discount
    val stock = variationData.stock
    val maxOrder = variationData.maxOrder
    val label = variationData.label
}