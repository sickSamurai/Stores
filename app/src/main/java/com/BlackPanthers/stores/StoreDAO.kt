package com.BlackPanthers.stores

import androidx.room.*

@Dao
interface StoreDAO {
    @Query("Select * From Store")
    fun getAll(): MutableList<Store>

    @Query("SELECT * FROM Store WHERE id = :id")
    fun getStoreById(id:Long): Store

    @Insert
    fun addStore(store: Store): Long

    @Update
    fun editStore(store: Store)

    @Delete
    fun deleteStore(store: Store)

}