package com.BlackPanthers.stores

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Store::class], version = 2)
abstract class StoreDB() : RoomDatabase() {
    abstract fun storeDAO(): StoreDAO
}