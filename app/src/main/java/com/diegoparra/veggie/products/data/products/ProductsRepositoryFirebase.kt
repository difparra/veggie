package com.diegoparra.veggie.products.data.products

import com.diegoparra.veggie.core.Either
import com.diegoparra.veggie.core.Failure
import com.diegoparra.veggie.core.map
import com.diegoparra.veggie.products.data.products.firebase.ProductDtosTransformations.toProduct
import com.diegoparra.veggie.products.data.products.firebase.ProductDtosTransformations.toTag
import com.diegoparra.veggie.products.data.products.firebase.ProductsApi
import com.diegoparra.veggie.products.domain.entities.Product
import com.diegoparra.veggie.products.domain.entities.Tag
import com.diegoparra.veggie.products.domain.entities.Variation
import com.diegoparra.veggie.products.domain.repositories.ProductsRepository
import javax.inject.Inject

class ProductsRepositoryFirebase (
    private val productsApi: ProductsApi
) : ProductsRepository {

    override suspend fun getTags(forceUpdate: Boolean, expirationTimeMillis: Long): Either<Failure, List<Tag>> {
        return productsApi.getTags().map { it.map { it.toTag() } }
    }

    override suspend fun getMainProductsByTagId(tagId: String, forceUpdate: Boolean, expirationTimeMillis: Long): Either<Failure, List<Product>> {
        return productsApi.getMainProductsByTagId(tagId).map { it.map { it.toProduct() } }
    }

    override suspend fun searchMainProductsByName(query: String, forceUpdate: Boolean, expirationTimeMillis: Long): Either<Failure, List<Product>> {
        //  TODO
        return Either.Left(Failure.ServerError)
    }

    override suspend fun getProductVariationsByMainId(mainId: String, forceUpdate: Boolean, expirationTimeMillis: Long): Either<Failure, List<Variation>> {
        //  TODO
        return Either.Left(Failure.ServerError)
    }

    override suspend fun getProduct(mainId: String, varId: String, forceUpdate: Boolean, expirationTimeMillis: Long): Either<Failure, Product> {
        //  TODO
        return Either.Left(Failure.ServerError)
    }
}