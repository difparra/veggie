package com.diegoparra.veggie.products.fakedata

import com.diegoparra.veggie.core.products.AdminMainData
import com.diegoparra.veggie.core.products.AdminProduct
import com.diegoparra.veggie.core.products.AdminVariationData
import com.diegoparra.veggie.core.products.Tag
import com.diegoparra.veggie.products.domain.entities.*
import java.util.*

object FakeProductsDatabase {

    val arandanos_bdj_lb = ProductBuilder()
            .createProduct(
                    tag = FakeTag.Fruits,
                    name = "Arándanos",
                    imageUrl = "https://res.cloudinary.com/p-fresh-pics/image/upload/v1592425703/Arandanos.png",
                    unit = FakeUnit.Bandeja(125),
                    price = 4500,
            ).addVariation(
                    unit = FakeUnit.Libra,
                    price = 18000
            ).build()

    val banano_lbMV = ProductBuilder()
            .createProduct(
                    tag = FakeTag.Fruits,
                    name = "Banano",
                    imageUrl = "https://res.cloudinary.com/p-fresh-pics/image/upload/v1592425704/Banano_Criollo.png",
                    unit = FakeUnit.Libra,
                    price = 1400,
                    discount = 0.1f,
                    details = listOf(FakeDetail.Maduro, FakeDetail.Verde)
            ).build()

    val granadilla_und = ProductBuilder()
            .createProduct(
                    tag = FakeTag.Fruits,
                    name = "Granadilla",
                    imageUrl = "https://res.cloudinary.com/p-fresh-pics/image/upload/v1592425705/Granadilla.png",
                    unit = FakeUnit.Unidad(200),
                    price = 1300,
                    suggestedLabel = FakeLabel.Recomendado
            ).build()

    val fresa_lbMV = ProductBuilder()
            .createProduct(
                    tag = FakeTag.Fruits,
                    name = "Fresa",
                    imageUrl = "https://res.cloudinary.com/p-fresh-pics/image/upload/v1592425705/Fresas.png",
                    unit = FakeUnit.Libra,
                    price = 3800,
                    details = listOf(FakeDetail.Maduro, FakeDetail.Verde)
            ).build()

    val arveja_lb = ProductBuilder()
            .createProduct(
                    tag = FakeTag.Vegetables,
                    name = "Arveja desgranada",
                    imageUrl = "https://res.cloudinary.com/p-fresh-pics/image/upload/v1592426889/Arveja_Desgranada.png",
                    unit = FakeUnit.Libra,
                    price = 5800,
                    stock = false,
                    suggestedLabel = FakeLabel.Popular
            ).build()

    val tomCherry_can = ProductBuilder()
            .createProduct(
                    tag = FakeTag.Vegetables,
                    name = "Tomate cherry",
                    imageUrl = "https://res.cloudinary.com/p-fresh-pics/image/upload/v1592426893/Tomate_Cherry.png",
                    unit = FakeUnit.Canastilla(125),
                    price = 2400,
                    discount = 0.05f
            ).build()

    val zanahoria_lb = ProductBuilder()
            .createProduct(
                    tag = FakeTag.Vegetables,
                    name = "Zanahoria",
                    imageUrl = "https://res.cloudinary.com/p-fresh-pics/image/upload/v1592426893/Zanahoria.png",
                    unit = FakeUnit.Libra,
                    price = 950
            ).build()

    val lomoRes_lb = ProductBuilder()
            .createProduct(
                    tag = FakeTag.Meats,
                    name = "Lomo de res",
                    imageUrl = "https://res.cloudinary.com/p-fresh-pics/image/upload/v1597724385/Carne.png",
                    unit = FakeUnit.Libra,
                    price = 13900
            ).build()

    val pechuga_lb = ProductBuilder()
            .createProduct(
                    tag = FakeTag.Meats,
                    name = "Pechuga",
                    imageUrl = "https://res.cloudinary.com/p-fresh-pics/image/upload/v1597724385/Pechuga_de_Pollo.png",
                    unit = FakeUnit.Libra,
                    price = 4500
            ).build()

    val sobrebarriga_lb = ProductBuilder()
            .createProduct(
                    tag = FakeTag.Meats,
                    name = "Sobrebarriga",
                    imageUrl = "https://res.cloudinary.com/p-fresh-pics/image/upload/v1597724385/Carne.png",
                    unit = FakeUnit.Libra,
                    price = 8600
            ).build()




    val tags = listOf(FakeTag.Fruits, FakeTag.Vegetables, FakeTag.Meats).map { it.tag }

    val products = listOf(
        arandanos_bdj_lb, banano_lbMV, granadilla_und, fresa_lbMV,
        arveja_lb, tomCherry_can, zanahoria_lb,
        lomoRes_lb, pechuga_lb, sobrebarriga_lb
    )

}



/*
    ------------------------------------------------------------------------------------------------
            HELPER CLASSES
    ------------------------------------------------------------------------------------------------
 */


sealed class FakeTag(val tag: Tag) {
    object Fruits : FakeTag(Tag(id = "Fruits", name = "Frutas"))
    object Vegetables : FakeTag(Tag(id = "Vegetables", name = "Verduras"))
    object Meats : FakeTag(Tag(id = "Meats", name = "Carnes"))
}

sealed class FakeDetail(val detail: String) {
    object Maduro: FakeDetail("Maduro")
    object Verde: FakeDetail("Verde")
}

sealed class FakeUnit(val unit: String, val weightGr: Int) {
    object Libra : FakeUnit("Libra", 500)
    class Bandeja(weightGr: Int) : FakeUnit("Bandeja", weightGr)
    class Canastilla(weightGr: Int) : FakeUnit("Canastilla", weightGr)
    class Unidad(weightGr: Int) : FakeUnit("Unidad", weightGr)
    //object Atado : FakeUnit("Atado", ProductConstants.NoWeightGr)
}

private sealed class FakeLabel(val str : String){
    object Recomendado : FakeLabel("Recomendado")
    object Popular : FakeLabel("Popular")
}


private class ProductBuilder {

    private lateinit var tagId: String
    private lateinit var mainId: String
    private lateinit var name: String
    private lateinit var imageUrl: String
    private lateinit var mainVarId: String
    private val variations = mutableListOf<AdminVariationData>()

    private fun String.normalise() =
            toLowerCase(Locale.ROOT)
                    .replace('á','a').replace('à', 'a')
                    .replace('é','e').replace('à', 'a')
                    .replace('í','i').replace('à', 'a')
                    .replace('ó','o').replace('à', 'a')
                    .replace('ú','u').replace('à', 'a')
                    .replace('ñ','n')
                    .replace(" ", "")


    fun createProduct(tag: FakeTag, mainId: String? = null, varId: String? = null,
                      name: String, imageUrl: String,
                      unit: FakeUnit, price: Int, discount: Float = 0.0f,
                      stock: Boolean = true, maxOrder: Int = 10,
                      suggestedLabel: FakeLabel? = null, details: List<FakeDetail>? = null
    ) : ProductBuilder {
        this.tagId = tag.tag.id
        this.mainId = mainId?.normalise() ?: name.normalise()
        this.name = name
        this.imageUrl = imageUrl
        this.mainVarId = varId?.normalise() ?: (name + unit.unit).normalise()
        variations.add(AdminVariationData(
                varId = this.mainVarId,
                relatedMainId = this.mainId,
                unit = unit.unit,
                weightGr = unit.weightGr,
                price = price,
                discount = discount,
                stock = stock,
                maxOrder = maxOrder,
                suggestedLabel = suggestedLabel?.str,
                detailOptions = details?.map { it.detail }
        ))
        return this
    }

    fun setMainId(mainId: String) : ProductBuilder {
        this.mainId = mainId.normalise()
        return this
    }

    fun setMainVarId(mainVarId: String) : ProductBuilder {
        val variation = variations.find { it.varId == mainVarId.normalise() }
        variation?.let {
            this@ProductBuilder.mainVarId = mainVarId.normalise()
        }
        return this
    }

    fun addVariation(varId: String? = null, unit: FakeUnit, price: Int, discount: Float = 0.0f,
                     stock: Boolean = true, maxOrder: Int = 10, suggestedLabel: FakeLabel? = null, details: List<FakeDetail>? = null
    ) : ProductBuilder {
        val finalVarId = getUniqueVarIdInMainId(varId?.normalise() ?: (name + unit.unit).normalise())
        variations.add(AdminVariationData(
                varId = finalVarId,
                relatedMainId = this.mainId,
                unit = unit.unit,
                weightGr = unit.weightGr,
                price = price,
                discount = discount,
                stock = stock,
                maxOrder = maxOrder,
                suggestedLabel = suggestedLabel?.str,
                detailOptions = details?.map { it.detail }
        ))
        return this
    }

    private fun getUniqueVarIdInMainId(varId: String) : String {
        var finalVarId = varId
        var varIdFound: Boolean
        var counter = 1
        do {
            if(variations.find { it.varId == finalVarId } != null){
                finalVarId += counter.toString()
                counter++
                varIdFound = true
            }else{
                varIdFound = false
            }
        }while (varIdFound)
        return finalVarId
    }

    fun build() : AdminProduct {
        return AdminProduct(
                tagId = tagId,
                mainData = AdminMainData(mainId = mainId, name = name, imageUrl = imageUrl, mainVariationId = mainVarId),
                variations = variations
        )
    }
}