package com.diegoparra.veggie.products.fakedata

import com.diegoparra.veggie.core.trimAllSpaces
import com.diegoparra.veggie.products.domain.entities.*

object FakeProductsDatabase {

    sealed class FakeTag(val tag: Tag) {
        object Fruits : FakeTag(Tag(id = "Fruits", name = "Frutas"))
        object Vegetables : FakeTag(Tag(id = "Vegetables", name = "Verduras"))
        object Meats : FakeTag(Tag(id = "Meats", name = "Carnes"))
    }

    sealed class FakeDetail(val detail: String) {
        object NoDetail: FakeDetail(ConstantsProducts.NoDetail)
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

    sealed class FakeLabel(val str : String){
        object Recomendado : FakeLabel("Recomendado")
        object Popular : FakeLabel("Popular")
        object None : FakeLabel(ConstantsProducts.NoLabel)
    }



    private fun createProduct(tag: FakeTag,
                              name: String, urlImage: String,
                              variations: List<VariationDataAdmin>,
                              mainVarId: String = variations[0].varId) : ProductAdmin {
        return ProductAdmin(
            tagId = tag.tag.id,
            mainData = MainDataAdmin(
                mainId = name.trimAllSpaces(),
                name = name,
                imageUrl = urlImage,
                mainVariationId = mainVarId
            ),
            variations = variations
        )
    }

    private fun createVariation(varId: String, unit: FakeUnit = FakeUnit.Libra,
                                price: Int, discount: Float = 0.0f,
                                stock: Boolean = true, maxOrder: Int = 10,
                                label: FakeLabel = FakeLabel.None,
                                details: List<FakeDetail> = listOf(FakeDetail.NoDetail)) : VariationDataAdmin {
        return VariationDataAdmin(
            varId = varId,
            unit = unit.unit, weightGr = unit.weightGr,
            price = price, discount = discount,
            stock = stock, maxOrder = maxOrder,
            suggestedLabel = label.str, detailOptions = details.map { it.detail }
        )
    }



    val arandanos_bdj_lb = createProduct(
        tag = FakeTag.Fruits,
        name = "Arandanos",
        urlImage = "https://res.cloudinary.com/p-fresh-pics/image/upload/v1592425703/Arandanos.png",
        variations = listOf(
            createVariation(
                varId = "ArandanosBandeja",
                unit = FakeUnit.Bandeja(125),
                price = 4500
            ),
            createVariation(
                varId = "ArandanosLibra",
                unit = FakeUnit.Libra,
                price = 18000
            )
        )
    )

    val banano_lbMV = createProduct(
        tag = FakeTag.Fruits,
        name = "Banano",
        urlImage = "https://res.cloudinary.com/p-fresh-pics/image/upload/v1592425704/Banano_Criollo.png",
        variations = listOf(
            createVariation(
                varId = "BananoLibra",
                unit = FakeUnit.Libra,
                price = 1400,
                discount = 0.1f,
                details = listOf(FakeDetail.Maduro, FakeDetail.Verde)
            )
        )
    )

    val granadilla_und = createProduct(
        tag = FakeTag.Fruits,
        name = "Granadilla",
        urlImage = "https://res.cloudinary.com/p-fresh-pics/image/upload/v1592425705/Granadilla.png",
        variations = listOf(
            createVariation(
                varId = "GranadillaUnidad",
                unit = FakeUnit.Unidad(200),
                price = 1300,
                label = FakeLabel.Recomendado
            )
        )
    )

    val fresa_lbMV = createProduct(
        tag = FakeTag.Fruits,
        name = "Fresa",
        urlImage = "https://res.cloudinary.com/p-fresh-pics/image/upload/v1592425705/Fresas.png",
        variations = listOf(
            createVariation(
                varId = "FresaLibra",
                unit = FakeUnit.Libra,
                price = 3800,
                details = listOf(FakeDetail.Maduro, FakeDetail.Verde)
            )
        )
    )

    val arveja_lb = createProduct(
        tag = FakeTag.Vegetables,
        name = "Arveja desgranada",
        urlImage = "https://res.cloudinary.com/p-fresh-pics/image/upload/v1592426889/Arveja_Desgranada.png",
        variations = listOf(
            createVariation(
                varId = "ArvejaDesgranadaLibra",
                unit = FakeUnit.Libra,
                price = 5800,
                stock = false,
                label = FakeLabel.Popular
            )
        )
    )

    val tomCherry_can = createProduct(
        tag = FakeTag.Vegetables,
        name = "Tomate cherry",
        urlImage = "https://res.cloudinary.com/p-fresh-pics/image/upload/v1592426893/Tomate_Cherry.png",
        variations = listOf(
            createVariation(
                varId = "TomateCherryCanastilla",
                unit = FakeUnit.Canastilla(125),
                price = 2400,
                discount = 0.05f
            )
        )
    )

    val zanahoria_lb = createProduct(
        tag = FakeTag.Vegetables,
        name = "Zanahoria",
        urlImage = "https://res.cloudinary.com/p-fresh-pics/image/upload/v1592426893/Zanahoria.png",
        variations = listOf(
            createVariation(
                varId = "ZanahoriaLibra",
                unit = FakeUnit.Libra,
                price = 950
            )
        )
    )

    val lomoRes_lb = createProduct(
        tag = FakeTag.Meats,
        name = "Lomo de res",
        urlImage = "https://res.cloudinary.com/p-fresh-pics/image/upload/v1597724385/Carne.png",
        variations = listOf(
            createVariation(
                varId = "LomoResLibra",
                unit = FakeUnit.Libra,
                price = 13900
            )
        )
    )

    val pechuga_lb = createProduct(
        tag = FakeTag.Meats,
        name = "Pechuga",
        urlImage = "https://res.cloudinary.com/p-fresh-pics/image/upload/v1597724385/Pechuga_de_Pollo.png",
        variations = listOf(
            createVariation(
                varId = "PechugaLibra",
                unit = FakeUnit.Libra,
                price = 4500
            )
        )
    )

    val sobrebarriga_lb = createProduct(
        tag = FakeTag.Meats,
        name = "Sobrebarriga",
        urlImage = "https://res.cloudinary.com/p-fresh-pics/image/upload/v1597724385/Carne.png",
        variations = listOf(
            createVariation(
                varId = "SobrebarrigaLibra",
                unit = FakeUnit.Libra,
                price = 8600
            )
        )
    )





    val tags = listOf(FakeTag.Fruits, FakeTag.Vegetables, FakeTag.Meats).map { it.tag }

    val products = listOf(
        arandanos_bdj_lb, banano_lbMV, granadilla_und, fresa_lbMV,
        arveja_lb, tomCherry_can, zanahoria_lb,
        lomoRes_lb, pechuga_lb, sobrebarriga_lb
    )

}