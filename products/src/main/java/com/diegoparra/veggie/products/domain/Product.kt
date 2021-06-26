package com.diegoparra.veggie.products.domain

//      ----------      PRODUCT        -------------------------------------------------------------

//  General product to use in the app. I will not normally get product with all variations,
//  but product with a specific variation.
data class Product(
    val tagId: String,
    val mainData: MainData,
    val variationData: VariationData
)

//  Will be mainly needed when inserting the products to the database.
//  In other words, will be required by the admin.
//  As for now, it is being used by the fakes.
data class ProductWithAllVariations(
    val tagId: String,
    val mainData: MainData,
    val variations: List<VariationData>
)

//      ----------      DATA        ----------------------------------------------------------------

data class MainData(
    val mainId: String,
    val name: String,
    val imageUrl: String,
    val mainVariationId: String
)

data class VariationData(
    val varId: String,
    val relatedMainId: String,
    val packet: String,         //  Unidad, Bandeja, Libra
    val weight: Int = -1,       //  -1 = Do not show
    val unit: String,           //  g, kg, ml, und
    val price: Int,
    val discount: Float,
    val stock: Boolean,
    val maxOrder: Int,
    private val suggestedLabel: String? = null,
    val detailOptions: List<String>? = null
) {
    val label
        get() = Label.createLabel(stock, discount, suggestedLabel)

    fun hasDetails() = !detailOptions.isNullOrEmpty()
}