package com.diegoparra.veggie.products.fakes.fake_products_dto_database

object VegetablesDto {

    private val acelga = ProductDtoBuilder()
        .createProduct(
            tag = FakeTagDto.Vegetables,
            name = "Acelga",
            imageUrl = "https://res.cloudinary.com/p-fresh-pics/image/upload/v1592526994/Acelga.png",
            unit = FakeUnitDto.Libra,
            price = 1500
        ).build()

    private val apio = ProductDtoBuilder()
        .createProduct(
            tag = FakeTagDto.Vegetables,
            name = "Apio",
            imageUrl = "https://res.cloudinary.com/p-fresh-pics/image/upload/v1592426464/Apio.png",
            unit = FakeUnitDto.Atado,
            price = 1900
        ).build()

    private val aromatica = ProductDtoBuilder()
        .createProduct(
            tag = FakeTagDto.Vegetables,
            name = "Aromática",
            imageUrl = "https://res.cloudinary.com/p-fresh-pics/image/upload/v1592426464/Aromatica.png",
            unit = FakeUnitDto.Atado,
            price = 1200
        ).build()

    private val cebollin = ProductDtoBuilder()
        .createProduct(
            tag = FakeTagDto.Vegetables,
            name = "Cebollín",
            imageUrl = "https://res.cloudinary.com/p-fresh-pics/image/upload/v1592426464/Cebollin.png",
            unit = FakeUnitDto.Bandeja(-1),
            price = 3100
        ).build()

    private val cilantro = ProductDtoBuilder()
        .createProduct(
            tag = FakeTagDto.Vegetables,
            name = "Cilantro",
            imageUrl = "https://res.cloudinary.com/p-fresh-pics/image/upload/v1592426463/Cilantro.png",
            unit = FakeUnitDto.Atado,
            price = 900
        ).build()

    private val espinaca = ProductDtoBuilder()
        .createProduct(
            tag = FakeTagDto.Vegetables,
            name = "Espinaca",
            imageUrl = "https://res.cloudinary.com/p-fresh-pics/image/upload/v1592526994/Espinaca.png",
            unit = FakeUnitDto.Atado,
            price = 2900
        ).build()

    private val guascas = ProductDtoBuilder()
        .createProduct(
            tag = FakeTagDto.Vegetables,
            name = "Guascas",
            imageUrl = "https://res.cloudinary.com/p-fresh-pics/image/upload/v1592526995/Guascas.png",
            unit = FakeUnitDto.Atado,
            price = 1100
        ).build()

    private val lechugaBatavia = ProductDtoBuilder()
        .createProduct(
            tag = FakeTagDto.Vegetables,
            name = "Lechuga Batavia",
            imageUrl = "https://res.cloudinary.com/p-fresh-pics/image/upload/v1592426463/Lechuga_Batavia.png",
            unit = FakeUnitDto.Unidad(600),
            price = 1600
        ).build()

    private val lechugaHidroponica = ProductDtoBuilder()
        .createProduct(
            tag = FakeTagDto.Vegetables,
            name = "Lechuga hidropónica",
            imageUrl = "https://res.cloudinary.com/p-fresh-pics/image/upload/v1592426464/Lechuga_Hidroponica_Verde.png",
            unit = FakeUnitDto.Atado,
            price = 1500
        ).build()

    private val perejil = ProductDtoBuilder()
        .createProduct(
            tag = FakeTagDto.Vegetables,
            name = "Perejil",
            imageUrl = "https://res.cloudinary.com/p-fresh-pics/image/upload/v1592426465/Perejil_Crespo.png",
            unit = FakeUnitDto.Atado,
            price = 1100
        ).build()

    private val sabila = ProductDtoBuilder()
        .createProduct(
            tag = FakeTagDto.Vegetables,
            name = "Sábila",
            imageUrl = "https://res.cloudinary.com/p-fresh-pics/image/upload/v1592526994/Sabila.png",
            unit = FakeUnitDto.Unidad(-1),
            price = 1500
        ).build()

    private val arracacha = ProductDtoBuilder()
        .createProduct(
            tag = FakeTagDto.Vegetables,
            name = "Arracacha",
            imageUrl = "https://res.cloudinary.com/p-fresh-pics/image/upload/v1592426723/Arracacha.png",
            unit = FakeUnitDto.Unidad(300),
            price = 700
        ).build()

    private val papaCriolla = ProductDtoBuilder()
        .createProduct(
            tag = FakeTagDto.Vegetables,
            name = "Papa criolla",
            imageUrl = "https://res.cloudinary.com/p-fresh-pics/image/upload/v1592426723/Papa_Criolla.png",
            unit = FakeUnitDto.Unidad(100),
            price = 400
        ).build()

    private val papaPastusa = ProductDtoBuilder()
        .createProduct(
            tag = FakeTagDto.Vegetables,
            name = "Papa pastusa",
            imageUrl = "https://res.cloudinary.com/p-fresh-pics/image/upload/v1592426723/Papa_Pastusa.png",
            unit = FakeUnitDto.Unidad(140),
            price = 200
        ).addVariation(
            unit = FakeUnitDto.Otro("Paquete x 5 lb", -1),
            price = 2500,
            discount = 0.1f
        )
        .build()

    private val papaSabanera = ProductDtoBuilder()
        .createProduct(
            tag = FakeTagDto.Vegetables,
            name = "Papa sabanera",
            imageUrl = "https://res.cloudinary.com/p-fresh-pics/image/upload/v1592426723/Papa_Sabanera.png",
            unit = FakeUnitDto.Unidad(120),
            price = 300
        ).build()

    private val yuca = ProductDtoBuilder()
        .createProduct(
            tag = FakeTagDto.Vegetables,
            name = "Yuca",
            imageUrl = "https://res.cloudinary.com/p-fresh-pics/image/upload/v1592426723/Yuca.png",
            unit = FakeUnitDto.Unidad(500),
            price = 1200
        ).build()

    private val ahuyama = ProductDtoBuilder()
        .createProduct(
            tag = FakeTagDto.Vegetables,
            name = "Ahuyama",
            imageUrl = "https://res.cloudinary.com/p-fresh-pics/image/upload/v1592426890/Ahuyama.png",
            unit = FakeUnitDto.Otro("Kilo", 1000),
            price = 1300
        ).build()

    private val ajo = ProductDtoBuilder()
        .createProduct(
            tag = FakeTagDto.Vegetables,
            name = "Ajo",
            imageUrl = "https://res.cloudinary.com/p-fresh-pics/image/upload/v1592527082/Ajo.png",
            unit = FakeUnitDto.Otro("Paquete x 3 und", -1),
            price = 1500
        ).build()

    private val arveja = ProductDtoBuilder()
        .createProduct(
            tag = FakeTagDto.Vegetables,
            name = "Arveja",
            imageUrl = "https://res.cloudinary.com/p-fresh-pics/image/upload/v1592426889/Arveja.png",
            unit = FakeUnitDto.Libra,
            price = 4000
        ).build()

    private val arvejaDesgranada = ProductDtoBuilder()
        .createProduct(
            tag = FakeTagDto.Vegetables,
            name = "Arveja desgranada",
            imageUrl = "https://res.cloudinary.com/p-fresh-pics/image/upload/v1592426889/Arveja_Desgranada.png",
            unit = FakeUnitDto.Libra,
            price = 5500
        ).build()

    private val berenjena = ProductDtoBuilder()
        .createProduct(
            tag = FakeTagDto.Vegetables,
            name = "Berenjena",
            imageUrl = "https://res.cloudinary.com/p-fresh-pics/image/upload/v1592527082/Berenjena.png",
            unit = FakeUnitDto.Unidad(500),
            price = 1400
        ).build()

    private val brocoli = ProductDtoBuilder()
        .createProduct(
            tag = FakeTagDto.Vegetables,
            name = "Brocoli",
            imageUrl = "https://res.cloudinary.com/p-fresh-pics/image/upload/v1592527083/Brocoli.png",
            unit = FakeUnitDto.Unidad(500),
            price = 2200
        ).build()

    private val calabaza = ProductDtoBuilder()
        .createProduct(
            tag = FakeTagDto.Vegetables,
            name = "Calabaza",
            imageUrl = "https://res.cloudinary.com/p-fresh-pics/image/upload/v1592527082/Calabaza.png",
            unit = FakeUnitDto.Unidad(900),
            price = 2200
        ).build()

    private val cebollaCabezona = ProductDtoBuilder()
        .createProduct(
            tag = FakeTagDto.Vegetables,
            name = "Cebolla cabezona",
            imageUrl = "https://res.cloudinary.com/p-fresh-pics/image/upload/v1592426890/Cebolla_Cabezona_Roja.png",
            unit = FakeUnitDto.Unidad(400),
            price = 800
        ).build()

    private val cebollaLarga = ProductDtoBuilder()
        .createProduct(
            tag = FakeTagDto.Vegetables,
            name = "Cebolla larga",
            imageUrl = "https://res.cloudinary.com/p-fresh-pics/image/upload/v1592527082/Cebolla_Larga.png",
            unit = FakeUnitDto.Libra,
            price = 1200
        ).build()

    private val cebollaPuerro = ProductDtoBuilder()
        .createProduct(
            tag = FakeTagDto.Vegetables,
            name = "Cebolla puerro",
            imageUrl = "https://res.cloudinary.com/p-fresh-pics/image/upload/v1592426890/Cebolla_Puerro.png",
            unit = FakeUnitDto.Unidad(500),
            price = 1800
        ).build()

    private val champinones = ProductDtoBuilder()
        .createProduct(
            tag = FakeTagDto.Vegetables,
            name = "Champiñones",
            imageUrl = "https://res.cloudinary.com/p-fresh-pics/image/upload/v1592426891/Champinones.png",
            unit = FakeUnitDto.Otro("Tajados bandeja", 500),
            price = 3200
        ).addVariation(
            unit = FakeUnitDto.Otro("Enteros bandeja", 1500),
            price = 9800
        ).build()

    private val colicero = ProductDtoBuilder()
        .createProduct(
            tag = FakeTagDto.Vegetables,
            name = "Colicero",
            imageUrl = "https://res.cloudinary.com/p-fresh-pics/image/upload/v1592426891/Colicero.png",
            unit = FakeUnitDto.Unidad(300),
            price = 700
        ).build()

    private val coliflor = ProductDtoBuilder()
        .createProduct(
            tag = FakeTagDto.Vegetables,
            name = "Coliflor",
            imageUrl = "https://res.cloudinary.com/p-fresh-pics/image/upload/v1592426890/Coliflor.png",
            unit = FakeUnitDto.Unidad(500),
            price = 2100
        ).build()

    private val esparragos = ProductDtoBuilder()
        .createProduct(
            tag = FakeTagDto.Vegetables,
            name = "Espárragos",
            imageUrl = "https://res.cloudinary.com/p-fresh-pics/image/upload/v1592426891/Esparragos.png",
            unit = FakeUnitDto.Bandeja(500),
            price = 7000
        ).build()

    private val frijol = ProductDtoBuilder()
        .createProduct(
            tag = FakeTagDto.Vegetables,
            name = "Fríjol",
            imageUrl = "https://res.cloudinary.com/p-fresh-pics/image/upload/v1592426891/Frijol_Verde.png",
            unit = FakeUnitDto.Libra,
            price = 1900,
        ).build()

    private val frijolDesgranado = ProductDtoBuilder()
        .createProduct(
            tag = FakeTagDto.Vegetables,
            name = "Fríjol desgranado",
            imageUrl = "https://res.cloudinary.com/p-fresh-pics/image/upload/v1592426891/Frijol_Verde_Desgranado.png",
            unit = FakeUnitDto.Libra,
            price = 4200
        ).build()

    private val habichuela = ProductDtoBuilder()
        .createProduct(
            tag = FakeTagDto.Vegetables,
            name = "Habichuela",
            imageUrl = "https://res.cloudinary.com/p-fresh-pics/image/upload/v1592426891/Habichuela.png",
            unit = FakeUnitDto.Libra,
            price = 1200
        ).build()

    private val jengibre = ProductDtoBuilder()
        .createProduct(
            tag = FakeTagDto.Vegetables,
            name = "Jengibre",
            imageUrl = "https://res.cloudinary.com/p-fresh-pics/image/upload/v1592426891/Jengibre.png",
            unit = FakeUnitDto.Libra,
            price = 1200
        ).build()

    private val mazorca = ProductDtoBuilder()
        .createProduct(
            tag = FakeTagDto.Vegetables,
            name = "Mazorca",
            imageUrl = "https://res.cloudinary.com/p-fresh-pics/image/upload/v1592426892/Mazorca.png",
            unit = FakeUnitDto.Unidad(470),
            price = 1600
        ).build()

    private val pepino = ProductDtoBuilder()
        .createProduct(
            tag = FakeTagDto.Vegetables,
            name = "Pepino cohombro",
            imageUrl = "https://res.cloudinary.com/p-fresh-pics/image/upload/v1592426892/Pepino_Cohombro.png",
            unit = FakeUnitDto.Unidad(400),
            price = 1200
        ).build()

    private val pimenton = ProductDtoBuilder()
        .createProduct(
            tag = FakeTagDto.Vegetables,
            name = "Pimentón",
            imageUrl = "https://res.cloudinary.com/p-fresh-pics/image/upload/v1592426892/Pimenton.png",
            unit = FakeUnitDto.Unidad(300),
            price = 1700
        ).build()

    private val platano = ProductDtoBuilder()
        .createProduct(
            tag = FakeTagDto.Vegetables,
            name = "Plátano",
            imageUrl = "https://res.cloudinary.com/p-fresh-pics/image/upload/v1592527082/Platano_Harton_Amarillo.png",
            unit = FakeUnitDto.Unidad(450),
            price = 1200
        ).build()

    private val tomate = ProductDtoBuilder()
        .createProduct(
            tag = FakeTagDto.Vegetables,
            name = "Tomate chonto",
            imageUrl = "https://res.cloudinary.com/p-fresh-pics/image/upload/v1592426893/Tomate_Chonto.png",
            unit = FakeUnitDto.Unidad(220),
            price = 500
        ).build()

    private val tomateCherry = ProductDtoBuilder()
        .createProduct(
            tag = FakeTagDto.Vegetables,
            name = "Tomate cherry",
            imageUrl = "https://res.cloudinary.com/p-fresh-pics/image/upload/v1592426893/Tomate_Cherry.png",
            unit = FakeUnitDto.Canastilla(500),
            price = 1700
        ).build()

    private val zanahoria = ProductDtoBuilder()
        .createProduct(
            tag = FakeTagDto.Vegetables,
            name = "Zanahoria",
            imageUrl = "https://res.cloudinary.com/p-fresh-pics/image/upload/v1592426893/Zanahoria.png",
            unit = FakeUnitDto.Unidad(300),
            price = 800
        ).build()

    private val zapayo = ProductDtoBuilder()
        .createProduct(
            tag = FakeTagDto.Vegetables,
            name = "Zapayo",
            imageUrl = "https://res.cloudinary.com/p-fresh-pics/image/upload/v1592426893/Zapayo.png",
            unit = FakeUnitDto.Unidad(700),
            price = 1600
        ).build()

    private val zucchini = ProductDtoBuilder()
        .createProduct(
            tag = FakeTagDto.Vegetables,
            name = "Zucchini",
            imageUrl = "https://res.cloudinary.com/p-fresh-pics/image/upload/v1592426893/Zucchini_Verde.png",
            unit = FakeUnitDto.Unidad(400),
            price = 1200
        ).build()


    val vegetables = listOf(
        acelga, apio, aromatica, cebollin, cilantro, espinaca, guascas, lechugaBatavia,
        lechugaHidroponica, perejil, sabila, arracacha, papaCriolla, papaPastusa, papaSabanera,
        yuca, ahuyama, ajo, arveja, arvejaDesgranada, berenjena, brocoli, calabaza,
        cebollaCabezona, cebollaLarga, cebollaPuerro, champinones, colicero, coliflor,
        esparragos, frijol, frijolDesgranado, habichuela, jengibre, mazorca, pepino,
        pimenton, platano, tomate, tomateCherry, zanahoria, zapayo, zucchini
    )

}