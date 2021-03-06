package com.diegoparra.veggie.products.app.ui

import com.diegoparra.veggie.products.app.entities.ProductVariation

sealed class VariationUi {
    data class Header(
        val packet: String,
        val weight: Int,
        val unit: String
    ) : VariationUi()

    data class Item(
        val headerIsVisible: Boolean,
        val varId: String,
        val detail: String?,
        val packet: String,
        val weight: Int,
        val unit: String,
        val price: Int,
        val discount: Float,
        val stock: Boolean,
        val maxOrder: Int,
        val quantity: Int
    ) : VariationUi()

    companion object {

        fun getListToSubmit(variationsList: List<ProductVariation>): List<VariationUi> {
            return if (variationsList.any { it.hasDetails() }) {
                getListWithHeadersAndDetails(variationsList)
            } else {
                getBasicList(variationsList)
            }
        }

        private fun getBasicList(variationsList: List<ProductVariation>): List<VariationUi> {
            return variationsList.map {
                getItem(variation = it, detail = null, headerIsVisible = false)
            }
        }

        private fun getListWithHeadersAndDetails(variationsList: List<ProductVariation>): List<VariationUi> {
            val listToSubmit = mutableListOf<VariationUi>()
            variationsList.forEach { variation ->
                listToSubmit.add(Header(variation.packet, variation.weight, variation.unit))
                if (variation.details.isNullOrEmpty()) {
                    listToSubmit.add(
                        getItem(
                            variation = variation,
                            detail = null,
                            headerIsVisible = true
                        )
                    )
                } else {
                    variation.details.forEach { detail ->
                        listToSubmit.add(
                            getItem(
                                variation = variation,
                                detail = detail,
                                headerIsVisible = true
                            )
                        )
                    }
                }
            }
            return listToSubmit
        }

        private fun getItem(
            variation: ProductVariation,
            detail: String?,
            headerIsVisible: Boolean
        ): Item {
            return Item(
                headerIsVisible = headerIsVisible,
                varId = variation.varId,
                detail = detail,
                packet = variation.packet,
                weight = variation.weight,
                unit = variation.unit,
                price = variation.price,
                discount = variation.discount,
                stock = variation.stock,
                maxOrder = variation.maxOrder,
                quantity = variation.quantity(detail)
            )
        }
    }
}