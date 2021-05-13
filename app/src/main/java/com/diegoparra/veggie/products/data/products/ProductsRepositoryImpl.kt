package com.diegoparra.veggie.products.data.products

import androidx.room.withTransaction
import com.diegoparra.veggie.core.Either
import com.diegoparra.veggie.core.Failure
import com.diegoparra.veggie.products.data.products.room.ProductsDao
import com.diegoparra.veggie.products.data.products.room.ProductsTransformations.toProduct
import com.diegoparra.veggie.products.data.products.room.ProductsTransformations.toTag
import com.diegoparra.veggie.products.data.products.room.ProductsTransformations.toVariation
import com.diegoparra.veggie.products.domain.entities.Product
import com.diegoparra.veggie.products.domain.entities.Tag
import com.diegoparra.veggie.products.domain.entities.Variation
import com.diegoparra.veggie.products.domain.repositories.ProductsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ProductsRepositoryImpl @Inject constructor(
        private val productsDao: ProductsDao,
        private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : ProductsRepository {


    override suspend fun getTags(
            forceUpdate: Boolean,
            expirationTimeMillis: Long
    ): Either<Failure, List<Tag>> = withContext(dispatcher){
        val tags = productsDao.getAllTags()
        if(tags.isNullOrEmpty()){
            Either.Left(Failure.ProductsFailure.TagsNotFound)
        }else{
            Either.Right(tags.map { it.toTag() })
        }
    }

    override suspend fun getMainProductsByTagId(
            tagId: String,
            forceUpdate: Boolean,
            expirationTimeMillis: Long
    ): Either<Failure, List<Product>> = withContext(dispatcher){
        val prods = productsDao.getMainProductByTagId(tagId)
        if(prods.isNullOrEmpty()){
            Either.Left(Failure.ProductsFailure.ProductsNotFound)
        }else{
            Either.Right(prods.map { it.toProduct() })
        }
    }

    override suspend fun searchMainProductsByName(
            query: String,
            forceUpdate: Boolean,
            expirationTimeMillis: Long
    ): Either<Failure, List<Product>> = withContext(dispatcher){
        val results = productsDao.searchMainProdByName(query)
        if(results.isNullOrEmpty()){
            Either.Left(Failure.SearchFailure.NoSearchResults)
        }else{
            Either.Right(results.map { it.toProduct() })
        }
    }

    override suspend fun getProductVariationsByMainId(
            mainId: String,
            forceUpdate: Boolean,
            expirationTimeMillis: Long
    ): Either<Failure, List<Variation>> = withContext(dispatcher){
        val variations = productsDao.getProductVariationsByMainId(mainId)
        if(variations.isNullOrEmpty()){
            Either.Left(Failure.ProductsFailure.ProductsNotFound)
        }else{
            Either.Right(variations.map { it.toVariation() })
        }
    }

    override suspend fun getProduct(
            mainId: String, varId: String,
            forceUpdate: Boolean,
            expirationTimeMillis: Long): Either<Failure, Product> = withContext(dispatcher){
        val product = productsDao.getProduct(mainId = mainId, varId = varId)
        if(product == null){
            Either.Left(Failure.ProductsFailure.ProductsNotFound)
        }else{
            Either.Right(product.toProduct())
        }
    }


}