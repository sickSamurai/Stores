package com.panthers.stores

import android.app.Application
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.panthers.stores.database.StoreAPI
import com.panthers.stores.database.StoreDB

class App : Application() {
    companion object {
        lateinit var db: StoreDB
        lateinit var storeAPI: StoreAPI
    }

    private val migrationToAddUrlPhotoColumn = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE Store ADD COLUMN urlPhoto TEXT")
        }
    }

    override fun onCreate() {
        super.onCreate()
        storeAPI = StoreAPI.getInstance(this)!!
        db = Room.databaseBuilder(this, StoreDB::class.java, "StoreDB")
            .addMigrations(migrationToAddUrlPhotoColumn)
            .build()
    }
}