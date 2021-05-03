package com.diegoparra.veggie.products.domain.entities

/**
    - Entities more associated to the admin.
    - Information that will be inserted in the database managed by the admin.

    Additional notes:
    -   This file is not intended to host simple data that can be calculated in each client app,
    as the priceBeforeDiscount or the final label.
    - However, it could manage more complex or important data as profitForUser, profitForHome,
    which although can be calculated on each app based on a baseProfit, this data is really
    important, and admin should be able to change it without requiring users to reinstall the app.
 */

data class AdminProduct(
    val tagId: String,
    val mainData: AdminMainData,
    val variations: List<AdminVariationData>
)

data class AdminMainData(
    val mainId: String,
    val name: String,
    val imageUrl: String,
    val mainVariationId: String
)

data class AdminVariationData(
    val varId: String,
    val unit: String,       //  UnitOptions as well as labelOptions could be fetched from remoteConfig.
    val weightGr: Int = -1,
    val price: Int,
    val discount: Float,
    val stock: Boolean,
    val maxOrder: Int,
    val suggestedLabel: String? = null,
    val detailOptions: List<String>? = null
)