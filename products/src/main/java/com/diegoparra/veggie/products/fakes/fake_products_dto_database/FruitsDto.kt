package com.diegoparra.veggie.products.fakes.fake_products_dto_database

object FruitsDto {

    private val agraz = ProductDtoBuilder()
        .createProduct(
            tag = FakeTagDto.Fruits,
            name = "Agraz",
            imageUrl = "https://res.cloudinary.com/p-fresh-pics/image/upload/v1592425701/Agraz.png",
            unit = FakeUnitDto.Bandeja(125),
            price = 3800
        ).build()

    private val aguacateHass = ProductDtoBuilder()
        .createProduct(
            tag = FakeTagDto.Fruits,
            name = "Aguacate Hass",
            imageUrl = "https://res.cloudinary.com/p-fresh-pics/image/upload/v1592425704/Aguacate_Hass.png",
            unit = FakeUnitDto.Unidad(200),
            price = 1600,
            details = listOf(FakeDetailDto.Verde, FakeDetailDto.Maduro)
        ).build()

    private val aguacatePapelillo = ProductDtoBuilder()
        .createProduct(
            tag = FakeTagDto.Fruits,
            name = "Aguacate Papelillo",
            imageUrl = "https://res.cloudinary.com/p-fresh-pics/image/upload/v1592612272/Aguacate_Papelillo.png",
            unit = FakeUnitDto.Unidad(500),
            price = 3800,
            details = listOf(FakeDetailDto.Verde, FakeDetailDto.Maduro)
        ).build()

    private val arandanos = ProductDtoBuilder()
        .createProduct(
            tag = FakeTagDto.Fruits,
            name = "Arándanos",
            imageUrl = "https://res.cloudinary.com/p-fresh-pics/image/upload/v1592425703/Arandanos.png",
            unit = FakeUnitDto.Bandeja(125),
            price = 4500
        ).build()

    private val bananoBocadillo = ProductDtoBuilder()
        .createProduct(
            tag = FakeTagDto.Fruits,
            name = "Banano bocadillo",
            imageUrl = "https://res.cloudinary.com/p-fresh-pics/image/upload/v1592425704/Banano_Bocadillo.png",
            unit = FakeUnitDto.Unidad(80),
            price = 240,
            details = listOf(FakeDetailDto.Verde, FakeDetailDto.Maduro)
        ).addVariation(
            unit = FakeUnitDto.Libra,
            price = 1400
        ).build()

    private val banano = ProductDtoBuilder()
        .createProduct(
            tag = FakeTagDto.Fruits,
            name = "Banano",
            imageUrl = "https://res.cloudinary.com/p-fresh-pics/image/upload/v1592425704/Banano_Criollo.png",
            unit = FakeUnitDto.Unidad(80),
            price = 240,
            details = listOf(FakeDetailDto.Verde, FakeDetailDto.Maduro),
            discount = 0.1f
        ).addVariation(
            unit = FakeUnitDto.Otro("Racimo", 1500),
            price = 3500
        ).build()

    private val guatila = ProductDtoBuilder()
        .createProduct(
            tag = FakeTagDto.Fruits,
            name = "Cidra - Guatila",
            imageUrl = "https://res.cloudinary.com/p-fresh-pics/image/upload/v1592425703/Cidra.png",
            unit = FakeUnitDto.Unidad(700),
            price = 1400
        ).build()

    private val ciruela = ProductDtoBuilder()
        .createProduct(
            tag = FakeTagDto.Fruits,
            name = "Ciruela",
            imageUrl = "https://res.cloudinary.com/p-fresh-pics/image/upload/v1592425704/Ciruela_Roja.png",
            unit = FakeUnitDto.Bandeja(550),
            price = 1400,
            details = listOf(FakeDetailDto.Verde, FakeDetailDto.Maduro)
        ).addVariation(
            unit = FakeUnitDto.Libra,
            price = 1350
        ).build()

    private val coco = ProductDtoBuilder()
        .createProduct(
            tag = FakeTagDto.Fruits,
            name = "Coco",
            imageUrl = "https://res.cloudinary.com/p-fresh-pics/image/upload/v1592425704/Coco.png",
            unit = FakeUnitDto.Unidad(700),
            price = 5200
        ).build()

    private val curuba = ProductDtoBuilder()
        .createProduct(
            tag = FakeTagDto.Fruits,
            name = "Curuba",
            imageUrl = "https://res.cloudinary.com/p-fresh-pics/image/upload/v1592425704/Curuba.png",
            unit = FakeUnitDto.Unidad(140),
            price = 320,
            details = listOf(FakeDetailDto.Verde, FakeDetailDto.Maduro)
        ).build()

    private val durazno = ProductDtoBuilder()
        .createProduct(
            tag = FakeTagDto.Fruits,
            name = "Durazno",
            imageUrl = "https://res.cloudinary.com/p-fresh-pics/image/upload/v1592425704/Durazno_Nacional.png",
            unit = FakeUnitDto.Unidad(180),
            price = 700,
            discount = 0.2f
        ).build()

    private val feijoa = ProductDtoBuilder()
        .createProduct(
            tag = FakeTagDto.Fruits,
            name = "Feijoa",
            imageUrl = "https://res.cloudinary.com/p-fresh-pics/image/upload/v1592425705/Feijoa.png",
            unit = FakeUnitDto.Libra,
            price = 3200,
            stock = false
        ).build()

    private val fresa = ProductDtoBuilder()
        .createProduct(
            tag = FakeTagDto.Fruits,
            name = "Fresa",
            imageUrl = "https://res.cloudinary.com/p-fresh-pics/image/upload/v1592425705/Fresas.png",
            unit = FakeUnitDto.Libra,
            price = 3900,
            suggestedLabel = FakeLabelDto.Recomendado
        ).build()

    private val granadilla = ProductDtoBuilder()
        .createProduct(
            tag = FakeTagDto.Fruits,
            name = "Granadilla",
            imageUrl = "https://res.cloudinary.com/p-fresh-pics/image/upload/v1592425705/Granadilla.png",
            unit = FakeUnitDto.Unidad(200),
            price = 1300,
            suggestedLabel = FakeLabelDto.Popular
        ).build()

    private val guanabana = ProductDtoBuilder()
        .createProduct(
            tag = FakeTagDto.Fruits,
            name = "Guanábana",
            imageUrl = "https://res.cloudinary.com/p-fresh-pics/image/upload/v1592425705/Guanabana.png",
            unit = FakeUnitDto.Unidad(3500),
            price = 9800,
            details = listOf(FakeDetailDto.Verde, FakeDetailDto.Maduro)
        ).build()

    private val guayaba = ProductDtoBuilder()
        .createProduct(
            tag = FakeTagDto.Fruits,
            name = "Guayaba",
            imageUrl = "https://res.cloudinary.com/p-fresh-pics/image/upload/v1592425707/Guayaba_Pera.png",
            unit = FakeUnitDto.Unidad(200),
            price = 600
        ).build()

    private val kiwi = ProductDtoBuilder()
        .createProduct(
            tag = FakeTagDto.Fruits,
            name = "Kiwi",
            imageUrl = "https://res.cloudinary.com/p-fresh-pics/image/upload/v1592425707/Kiwi.png",
            unit = FakeUnitDto.Unidad(150),
            price = 1600,
            suggestedLabel = FakeLabelDto.Recomendado
        ).build()

    private val limon = ProductDtoBuilder()
        .createProduct(
            tag = FakeTagDto.Fruits,
            name = "Limón",
            imageUrl = "https://res.cloudinary.com/p-fresh-pics/image/upload/v1592526917/Limon_Tahiti.png",
            unit = FakeUnitDto.Unidad(90),
            price = 300
        ).addVariation(
            unit = FakeUnitDto.Libra,
            price = 1300,
            discount = 0.05f
        ).build()

    private val lulo = ProductDtoBuilder()
        .createProduct(
            tag = FakeTagDto.Fruits,
            name = "Lulo",
            imageUrl = "https://res.cloudinary.com/p-fresh-pics/image/upload/v1592425707/Lulo.png",
            unit = FakeUnitDto.Unidad(180),
            price = 600
        ).build()

    private val mandarina = ProductDtoBuilder()
        .createProduct(
            tag = FakeTagDto.Fruits,
            name = "Mandarina",
            imageUrl = "https://res.cloudinary.com/p-fresh-pics/image/upload/v1592425706/Mandarina_Arrayana.png",
            unit = FakeUnitDto.Unidad(120),
            price = 400
        ).addVariation(
            unit = FakeUnitDto.Libra,
            price = 1300,
            discount = 0.05f
        ).build()

    private val mango = ProductDtoBuilder()
        .createProduct(
            tag = FakeTagDto.Fruits,
            name = "Mango Tommy",
            imageUrl = "https://res.cloudinary.com/p-fresh-pics/image/upload/v1592425707/Mango.png",
            unit = FakeUnitDto.Unidad(600),
            price = 1800,
            details = listOf(FakeDetailDto.Verde, FakeDetailDto.Maduro)
        ).build()

    private val manzana = ProductDtoBuilder()
        .createProduct(
            tag = FakeTagDto.Fruits,
            name = "Manzana",
            imageUrl = "https://res.cloudinary.com/p-fresh-pics/image/upload/v1592425708/Manzana_Roja.png",
            unit = FakeUnitDto.Unidad(200),
            price = 1400
        ).addVariation(
            unit = FakeUnitDto.Otro("Bandeja x 6 und", -1),
            price = 5900,
            discount = 0.1f
        ).build()

    private val manzanaVerde = ProductDtoBuilder()
        .createProduct(
            tag = FakeTagDto.Fruits,
            name = "Manzana Verde",
            imageUrl = "https://res.cloudinary.com/p-fresh-pics/image/upload/v1592425709/Manzana_Verde.png",
            unit = FakeUnitDto.Unidad(200),
            price = 1400
        ).addVariation(
            unit = FakeUnitDto.Otro("Bandeja x 6 und", -1),
            price = 6500
        ).build()

    private val maracuya = ProductDtoBuilder()
        .createProduct(
            tag = FakeTagDto.Fruits,
            name = "Maracuyá",
            imageUrl = "https://res.cloudinary.com/p-fresh-pics/image/upload/v1592425709/Maracuya.png",
            unit = FakeUnitDto.Unidad(400),
            price = 1400
        ).build()

    private val melon = ProductDtoBuilder()
        .createProduct(
            tag = FakeTagDto.Fruits,
            name = "Melón",
            imageUrl = "https://res.cloudinary.com/p-fresh-pics/image/upload/v1592013733/Melon.png",
            unit = FakeUnitDto.Unidad(1600),
            price = 5200,
            discount = 0.15f
        ).build()

    private val mora = ProductDtoBuilder()
        .createProduct(
            tag = FakeTagDto.Fruits,
            name = "Mora",
            imageUrl = "https://res.cloudinary.com/p-fresh-pics/image/upload/v1592425708/Mora.png",
            unit = FakeUnitDto.Libra,
            price = 1900
        ).build()

    private val naranjaValencia = ProductDtoBuilder()
        .createProduct(
            tag = FakeTagDto.Fruits,
            name = "Naranja Valencia",
            imageUrl = "https://res.cloudinary.com/p-fresh-pics/image/upload/v1592425709/Naranja_Valencia.png",
            unit = FakeUnitDto.Unidad(250),
            price = 400,
            discount = 0.1f
        ).build()

    private val naranjaTangelo = ProductDtoBuilder()
        .createProduct(
            tag = FakeTagDto.Fruits,
            name = "Naranja Tangelo",
            imageUrl = "https://res.cloudinary.com/p-fresh-pics/image/upload/v1592425708/Naranja_Tangelo.png",
            unit = FakeUnitDto.Unidad(250),
            price = 900
        ).build()

    private val papaya = ProductDtoBuilder()
        .createProduct(
            tag = FakeTagDto.Fruits,
            name = "Papaya",
            imageUrl = "https://res.cloudinary.com/p-fresh-pics/image/upload/v1592425709/Papaya.png",
            unit = FakeUnitDto.Unidad(2200),
            price = 4900
        ).build()

    private val pera = ProductDtoBuilder()
        .createProduct(
            tag = FakeTagDto.Fruits,
            name = "Pera",
            imageUrl = "https://res.cloudinary.com/p-fresh-pics/image/upload/v1592425709/Pera_Importada.png",
            unit = FakeUnitDto.Unidad(300),
            price = 1400
        ).build()

    private val pina = ProductDtoBuilder()
        .createProduct(
            tag = FakeTagDto.Fruits,
            name = "Piña Golden",
            imageUrl = "https://res.cloudinary.com/p-fresh-pics/image/upload/v1592425711/Pina_Golden.png",
            unit = FakeUnitDto.Unidad(1800),
            price = 4200
        ).build()

    private val pitaya = ProductDtoBuilder()
        .createProduct(
            tag = FakeTagDto.Fruits,
            name = "Pitaya",
            imageUrl = "https://res.cloudinary.com/p-fresh-pics/image/upload/v1592425710/Pitaya.png",
            unit = FakeUnitDto.Unidad(400),
            price = 3100,
            suggestedLabel = FakeLabelDto.Recomendado
        ).build()

    private val sandiaBaby = ProductDtoBuilder()
        .createProduct(
            tag = FakeTagDto.Fruits,
            name = "Sandía Baby",
            imageUrl = "https://res.cloudinary.com/p-fresh-pics/image/upload/v1592425710/Sandia_Baby.png",
            unit = FakeUnitDto.Unidad(2200),
            price = 4200
        ).build()

    private val tomateArbol = ProductDtoBuilder()
        .createProduct(
            tag = FakeTagDto.Fruits,
            name = "Tomate de árbol",
            imageUrl = "https://res.cloudinary.com/p-fresh-pics/image/upload/v1592425702/Tomate_de_Arbol.png",
            unit = FakeUnitDto.Unidad(200),
            price = 400
        ).build()

    private val uchuvas = ProductDtoBuilder()
        .createProduct(
            tag = FakeTagDto.Fruits,
            name = "Uchuvas",
            imageUrl = "https://res.cloudinary.com/p-fresh-pics/image/upload/v1592425702/Uchuvas.png",
            unit = FakeUnitDto.Canastilla(-1),
            price = 1700
        ).build()

    private val uvaIsabela = ProductDtoBuilder()
        .createProduct(
            tag = FakeTagDto.Fruits,
            name = "Uva Isabela",
            imageUrl = "https://res.cloudinary.com/p-fresh-pics/image/upload/v1592526917/Uva_Isabela.png",
            unit = FakeUnitDto.Bandeja(500),
            price = 1100
        ).build()

    private val uvaRoja = ProductDtoBuilder()
        .createProduct(
            tag = FakeTagDto.Fruits,
            name = "Uva roja Nacional",
            imageUrl = "https://res.cloudinary.com/p-fresh-pics/image/upload/v1592425703/Uva_Roja.png",
            unit = FakeUnitDto.Libra,
            price = 3900
        ).build()


    val fruits = listOf(
        agraz, aguacateHass, aguacatePapelillo, arandanos, bananoBocadillo, banano,
        guatila, ciruela, coco, curuba, durazno, feijoa, fresa, granadilla, guanabana,
        guayaba, kiwi, limon, lulo, mandarina, mango, manzana, manzanaVerde,
        maracuya, melon, mora, naranjaValencia, naranjaTangelo, papaya,
        pera, pina, pitaya, sandiaBaby, tomateArbol, uchuvas, uvaIsabela, uvaRoja
    )

}