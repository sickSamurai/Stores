package com.panthers.stores.database.dao

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.*
import com.panthers.stores.database.entities.Store

@Dao
interface StoreDAO {
    @Query("Select * From Store")
    fun getAll(): LiveData<MutableList<Store>>

    @Query("SELECT * FROM Store WHERE id = :id")
    fun getStoreByID(id: Long): Store

    @Insert
    suspend fun addStore(store: Store): Long

    @Update
    suspend fun editStore(store: Store)

    @Delete
    suspend fun deleteStore(store: Store)

}