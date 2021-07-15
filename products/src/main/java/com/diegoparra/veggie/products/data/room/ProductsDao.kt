package com.diegoparra.veggie.products.data.room

import androidx.room.*
import com.diegoparra.veggie.core.android.LocalUpdateHelper
import com.diegoparra.veggie.core.kotlin.removeCaseAndAccents

@Dao
abstract class ProductsDao : LocalUpdateHelper.RoomDb<ProductUpdateRoom> {

    @Transaction
    @Query("Select * from Tags")
    abstract suspend fun getAllTags(): List<TagEntity>

    @Transaction
    @Query("Select * from Main where relatedTagId = :tagId order by name")
    abstract suspend fun getMainProductsByTagId(tagId: String): List<MainWithMainVariation>

    @Transaction
    @Query("Select * from Main where normalised_name like ('%' || :normalisedQuery || '%') order by name")
    protected abstract suspend fun _searchMainProdByName(normalisedQuery: String): List<MainWithMainVariation>
    suspend fun searchMainProdByName(query: String) =
        _searchMainProdByName(query.removeCaseAndAccents())

    @Query("Select * from Variations where relatedMainId = :mainId")
    abstract suspend fun getProductVariationsByMainId(mainId: String): List<VariationEntity>

    @Transaction
    @Query("Select * from Variations where relatedMainId = :mainId and varId = :varId")
    abstract suspend fun getProduct(mainId: String, varId: String): VariationWithMain?


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
    abstract override suspend fun getLastUpdatedTime(): Long?


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
    protected abstract suspend fun getVariationIdsInMainId(relatedMainId: String): List<String>


    /*
        IMPORTANT:
            Updates must run in a transaction, so that if for some reason the operation was cancelled,
            revert the products updated at that time, so that the lastUpdatedTime is also not
            modified and is consistent.
            This is important because the following scenario: remote products updated after certain
            time are fetched in a list, but not a sorted list by date. That means first product in the
            list could have been updated on Monday, second on Friday, and third on Tuesday.
            If updating local database fail as soon as second product is updated, and there is no
            transaction support, now the lastUpdatedTime will be Friday, and therefore, the third
            product will not be updated neither in this process (because of the failure) nor in
            subsequent process (as it is before the lastUpdatedTime).
            With transaction support, if failed occur in second product, transaction will revert
            the changes made with the first product, so the lastUpdatedTime will be kept as initial,
            before Monday.
     */

    @Transaction
    open override suspend fun updateItems(
        itemsUpdated: List<ProductUpdateRoom>,
        itemsDeleted: List<String>
    ) {
        if (!itemsDeleted.isNullOrEmpty()) {
            deleteMains(itemsDeleted)
        }
        if (!itemsUpdated.isNullOrEmpty()) {
            insertOrUpdateProducts(itemsUpdated)
        }
    }

    @Transaction
    protected open suspend fun insertOrUpdateProducts(products: List<ProductUpdateRoom>) {
        if (products.isNullOrEmpty()) return
        upsertMains(products.map { it.mainEntity })
        for (product in products) {
            deleteLocalVariationsNotInMainUpdate(product.mainEntity.mainId, product.variations)
            upsertVariations(product.variations)
        }
    }

    @Transaction
    protected open suspend fun deleteLocalVariationsNotInMainUpdate(
        mainId: String,
        variations: List<VariationEntity>
    ) {
        val localVarIds = getVariationIdsInMainId(mainId)
        val networkVarIds = variations.map { it.varId }
        val varIdsToDelete = localVarIds.filterNot { it in networkVarIds }
        if (!varIdsToDelete.isNullOrEmpty()) {
            deleteVariations(varIdsToDelete)
        }
    }

}