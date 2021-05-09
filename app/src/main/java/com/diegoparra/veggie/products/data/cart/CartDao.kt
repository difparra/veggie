package com.diegoparra.veggie.products.data.cart

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged

@Dao
abstract class CartDao {

    @Query("Select * from Cart")
    protected abstract fun _getAllCartItems() : Flow<List<CartEntity>>
    fun getAllCartItems() = _getAllCartItems().distinctUntilChanged()

    @Query("Select quantity from Cart where mainId = :mainId and varId = :varId and detail = :detail")
    protected abstract fun _getQuantityItem(mainId: String, varId: String, detail: String) : Flow<Int?>
    fun getQuantityItem(prodIdRoom: ProdIdRoom) =
            _getQuantityItem(prodIdRoom.mainId, prodIdRoom.varId, prodIdRoom.detail).distinctUntilChanged()

    @Query("Select Sum(quantity) from Cart where mainId = :mainId")
    protected abstract fun _getQuantityByMainId(mainId: String) : Flow<Int?>
    fun getQuantityByMainId(mainId: String) = _getQuantityByMainId(mainId).distinctUntilChanged()

    @Query("Select * from Cart where mainId = :mainId and varId = :varId")
    protected abstract fun _getVariations(mainId: String, varId: String) : Flow<List<CartEntity>>
    fun getVariations(mainId: String, varId: String) = _getVariations(mainId, varId).distinctUntilChanged()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun addItem(item: CartEntity)

    @Query("Delete from Cart where mainId = :mainId and varId = :varId and detail = :detail")
    protected abstract fun _deleteItem(mainId: String, varId: String, detail: String)
    fun deleteItem(prodIdRoom: ProdIdRoom) = _deleteItem(prodIdRoom.mainId, prodIdRoom.varId, prodIdRoom.detail)

    @Query("Update Cart set quantity = :newQuantity where mainId = :mainId and varId = :varId and detail = :detail")
    protected abstract fun _updateQuantityItem(mainId: String, varId: String, detail: String, newQuantity: Int)
    fun updateQuantityItem(prodIdRoom: ProdIdRoom, newQuantity: Int) = _updateQuantityItem(prodIdRoom.mainId, prodIdRoom.varId, prodIdRoom.detail, newQuantity)

    @Query("Select * from Cart where mainId = :mainId and varId = :varId and detail = :detail")
    protected abstract fun _getItem(mainId: String, varId: String, detail: String) : CartEntity?
    fun getItem(prodIdRoom: ProdIdRoom) = _getItem(prodIdRoom.mainId, prodIdRoom.varId, prodIdRoom.detail)

    @Query("Select quantity from Cart where mainId = :mainId and varId = :varId and detail = :detail")
    protected abstract fun _getCurrentQuantityItem(mainId: String, varId: String, detail: String) : Int?
    fun getCurrentQuantityItem(prodIdRoom: ProdIdRoom) = _getCurrentQuantityItem(prodIdRoom.mainId, prodIdRoom.varId, prodIdRoom.detail)

    @Query("Select mainId, varId, detail from Cart")
    protected abstract fun _getProductIds() : Flow<List<ProdIdRoom>>
    fun getProductIds() = _getProductIds().distinctUntilChanged()
}