package com.diegoparra.veggie.products.domain.entities

/*
    Product used by the ui in the client app
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
    val description = Description(price, discount, unit, weightGr)
}