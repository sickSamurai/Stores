package com.panthers.stores.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.panthers.stores.database.dao.StoreDAO
import com.panthers.stores.database.entities.Store

@Database(entities = [Store::class], version = 2)
abstract class StoreDB() : RoomDatabase() {
    abstract fun storeDAO(): StoreDAO
}