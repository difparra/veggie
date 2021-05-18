package com.diegoparra.veggie.products.cart.data.room

data class ProdIdRoom(
        val mainId: String,
        val varId: String,
        val detail: String
){
    companion object{
        const val NO_DETAIL = ""
    }
}