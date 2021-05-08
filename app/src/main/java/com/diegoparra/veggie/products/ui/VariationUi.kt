package com.diegoparra.veggie.products.ui

import com.diegoparra.veggie.products.domain.entities.ProdVariationWithQuantities

sealed class VariationUi {
    data class Header(
            val unit: String,
            val weightGr: Int
    ) : VariationUi()
    data class Item(
            val headerIsVisible: Boolean,
            val varId: String,
            val detail: String?,
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
            return if(variationsList.any { it.hasDetails() } ){
                getListWithHeadersAndDetails(variationsList)
            }else{
                getBasicList(variationsList)
            }
        }

        private fun getBasicList(variationsList: List<ProdVariationWithQuantities>) : List<VariationUi> {
            return variationsList.map {
                getItem(variation = it, detail = null, headerIsVisible = false)
            }
        }
        private fun getListWithHeadersAndDetails(variationsList: List<ProdVariationWithQuantities>) : List<VariationUi> {
            val listToSubmit = mutableListOf<VariationUi>()
            variationsList.forEach { variation ->
                listToSubmit.add(Header(variation.unit, variation.weightGr))
                if(variation.details.isNullOrEmpty()){
                    listToSubmit.add(getItem(variation = variation, detail = null, headerIsVisible = true))
                }else{
                    variation.details.forEach { detail ->
                        listToSubmit.add(getItem(variation = variation, detail = detail, headerIsVisible = true))
                    }
                }
            }
            return listToSubmit
        }

        private fun getItem(variation: ProdVariationWithQuantities, detail: String?, headerIsVisible: Boolean) : Item {
            return Item(
                    headerIsVisible = headerIsVisible,
                    varId = variation.varId,
                    detail = detail,
                    unit = variation.unit,
                    weightGr = variation.weightGr,
                    price = variation.price,
                    discount = variation.discount,
                    stock = variation.stock,
                    maxOrder = variation.maxOrder,
                    quantity = variation.quantity(detail)
            )
        }
    }
}