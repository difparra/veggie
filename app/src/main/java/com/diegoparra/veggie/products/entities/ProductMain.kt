package com.diegoparra.veggie.products.entities

import com.diegoparra.veggie.products.domain.Product

/**
 * Classes with prefix Product are normally products ready to be used in the ui.
 */

data class ProductMain(
    private val product: Product,
    val quantity: Int
){
    val mainId = product.mainId
    val name = product.name
    val imageUrl = product.imageUrl
    val unit = product.unit
    val weightGr = product.weightGr
    val price = product.price
    val discount = product.discount
    val stock = product.stock
    val label = product.label

}