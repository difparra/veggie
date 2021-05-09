package com.diegoparra.veggie.products.data.cart

import com.diegoparra.veggie.products.domain.entities.CartItem
import com.diegoparra.veggie.products.domain.entities.ProductId

object CartTransformations {

    fun ProdIdRoom.toProductId() : ProductId {
        val detailDomain = if(detail == ProdIdRoom.NO_DETAIL) null else detail
        return ProductId(mainId = mainId, varId = varId, detail = detailDomain)
    }

    fun ProductId.toProdIdRoom() : ProdIdRoom {
        return ProdIdRoom(mainId = mainId, varId = varId, detail = detail ?: ProdIdRoom.NO_DETAIL)
    }

    fun CartItem.toCartEntity() : CartEntity {
        return CartEntity(prodId = productId.toProdIdRoom(), quantity = quantity)
    }

    fun CartEntity.toCartItem() : CartItem {
        return CartItem(productId = prodId.toProductId(), quantity = quantity)
    }

    fun List<CartEntity>.toMapQuantitiesByDetail() : Map<String?, Int> {
        val qtyMap = mutableMapOf<String?, Int>()
        this.forEach {
            val detailDomain = if(it.prodId.detail == ProdIdRoom.NO_DETAIL) null else it.prodId.detail
            qtyMap[detailDomain] = it.quantity
        }
        return qtyMap
    }

}