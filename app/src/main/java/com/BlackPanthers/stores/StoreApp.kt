package com.BlackPanthers.stores

import android.app.Application
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class  StoreApp: Application() {
    companion object{
        lateinit var db: StoreDB
    }

    override fun onCreate() {
        super.onCreate()
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE Store ADD COLUMN urlPhoto TEXT")
            }
        }
        db = Room.databaseBuilder(this, StoreDB::class.java, "StoreDB").addMigrations(MIGRATION_1_2).build()
    }
}