package com.diegoparra.veggie.products.room

import androidx.room.*
import com.diegoparra.veggie.core.customNormalisation

@Dao
abstract class ProductsDao {

    @Transaction
    @Query("Select * from Tags")
    abstract suspend fun getAllTags() : List<TagEntity>

    @Transaction
    @Query("Select * from Main where relatedTagId = :tagId order by name")
    abstract suspend fun getMainProductsByTagId(tagId: String) : List<MainWithMainVariation>

    @Transaction
    @Query("Select * from Main where normalised_name like ('%' || :normalisedQuery || '%') order by name")
    protected abstract suspend fun _searchMainProdByName(normalisedQuery: String) : List<MainWithMainVariation>
    suspend fun searchMainProdByName(query: String) = _searchMainProdByName(query.customNormalisation())

    @Query("Select * from Variations where relatedMainId = :mainId")
    abstract suspend fun getProductVariationsByMainId(mainId: String) : List<VariationEntity>

    @Transaction
    @Query("Select * from Variations where relatedMainId = :mainId and varId = :varId")
    abstract suspend fun getProduct(mainId: String, varId: String) : VariationWithMain?


    /*
        --------------------------------------------------------------------------------------------
                Update database methods
        --------------------------------------------------------------------------------------------
     */

    @Insert
    protected abstract suspend fun insertTags(tags: List<TagEntity>)
    @Query("Delete from Tags")
    protected abstract suspend fun deleteAllTags()
    @Transaction
    open suspend fun updateAllTags(tags: List<TagEntity>) {
        deleteAllTags()
        insertTags(tags)
    }


    @Query("Select MAX(updatedAtInMillis) from Main")
    abstract suspend fun getLastProdUpdatedAtInMillis() : Long?

    //      ----------------------------------------

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract suspend fun upsertMains(main: List<MainEntity>)
    @Query("Delete from Main where mainId in (:mainIds)")
    protected abstract suspend fun deleteMains(mainIds: List<String>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract suspend fun upsertVariations(variations: List<VariationEntity>)
    @Query("Delete from Variations where varId in (:varIds)")
    protected abstract suspend fun deleteVariations(varIds: List<String>)
    @Query("Select varId from Variations where relatedMainId = :relatedMainId")
    protected abstract suspend fun getVariationIdsInMainId(relatedMainId: String) : List<String>


    @Transaction
    open suspend fun updateProducts(mainProdsIdToDelete: List<String>, prodsToUpdate: List<ProductUpdateRoom>){
        if(!mainProdsIdToDelete.isNullOrEmpty()){
            deleteMains(mainProdsIdToDelete)
        }
        if(!prodsToUpdate.isNullOrEmpty()){
            insertOrUpdateProducts(prodsToUpdate)
        }
    }

    @Transaction
    protected open suspend fun insertOrUpdateProducts(products: List<ProductUpdateRoom>){
        if(products.isNullOrEmpty())    return
        upsertMains(products.map { it.mainEntity })
        for(product in products){
            deleteLocalVariationsNotInMainUpdate(product.mainEntity.mainId, product.variations)
            upsertVariations(product.variations)
        }
    }
    @Transaction
    protected open suspend fun deleteLocalVariationsNotInMainUpdate(mainId: String, variations: List<VariationEntity>){
        val localVarIds = getVariationIdsInMainId(mainId)
        val networkVarIds = variations.map { it.varId }
        val varIdsToDelete = localVarIds.filterNot { it in networkVarIds }
        if(!varIdsToDelete.isNullOrEmpty()){
            deleteVariations(varIdsToDelete)
        }
    }

}