package com.BlackPanthers.stores

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Store")
data class Store(@PrimaryKey(autoGenerate = true) var id: Long = 0, var name: String) {
    var phone: String = ""
    var website: String = ""
    var urlPhoto: String = ""
    var isFavorite: Boolean = false

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Store

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

}

