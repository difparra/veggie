package com.diegoparra.veggie.products.ui

import com.diegoparra.veggie.products.domain.entities.ProdVariationWithQuantities

sealed class VariationUi {
    data class Header(
            val unit: String,
            val weightGr: Int
    ) : VariationUi()
    data class ItemForHeader(
            val varId: String,
            val detail: String?,
            val price: Int,
            val discount: Float,
            val stock: Boolean,
            val maxOrder: Int,
            val quantity: Int
    ) : VariationUi()
    data class BasicItem(
            val varId: String,
            val unit: String,
            val weightGr: Int,
            val price: Int,
            val discount: Float,
            val stock: Boolean,
            val maxOrder: Int,
            val quantity: Int
    ) : VariationUi()

    companion object {
        fun getListToSubmit(variationsList: List<ProdVariationWithQuantities>) : List<VariationUi> {
            return if(variationsList.any { it.hasDetails} ){
                getListWithDetails(variationsList)
            }else{
                getBasicList(variationsList)
            }
        }
        private fun getListWithDetails(variationsList: List<ProdVariationWithQuantities>) : List<VariationUi> {
            val listToSubmit = mutableListOf<VariationUi>()
            variationsList.forEach { variation ->
                listToSubmit.add(Header(variation.unit, variation.weightGr))
                if(variation.details.isNullOrEmpty()){
                    listToSubmit.add(ItemForHeader(variation.varId, null, variation.price, variation.discount, variation.stock, variation.maxOrder, variation.quantity(null)))
                }else{
                    variation.details.forEach { detail ->
                        listToSubmit.add(ItemForHeader(variation.varId, detail, variation.price, variation.discount, variation.stock, variation.maxOrder, variation.quantity(detail)))
                    }
                }
            }
            return listToSubmit
        }
        private fun getBasicList(variationsList: List<ProdVariationWithQuantities>) : List<VariationUi> {
            return variationsList.map {
                BasicItem(it.varId, it.unit, it.weightGr, it.price, it.discount, it.stock, it.maxOrder, it.quantity(null))
            }
        }
    }
}