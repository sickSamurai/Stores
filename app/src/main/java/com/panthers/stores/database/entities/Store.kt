package com.panthers.stores.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Store")
data class Store(@PrimaryKey(autoGenerate = true) var id: Long = 0, var name: String) {
    var phone: String = ""
    var website: String = ""
    var urlPhoto: String = ""
    var isFavorite: Boolean = false

    constructor() : this(0, "")

}

