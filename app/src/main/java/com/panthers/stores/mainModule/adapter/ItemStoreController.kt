package com.panthers.stores.mainModule.adapter

import com.panthers.stores.database.entities.Store

interface ItemStoreController {
    fun onItemStoreClick(store: Store)
    fun onItemStoreLongClick(store: Store):Boolean
    fun onCheckBoxFavoriteClick(store: Store)
}