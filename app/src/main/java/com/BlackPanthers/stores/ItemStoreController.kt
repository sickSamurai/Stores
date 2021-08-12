package com.BlackPanthers.stores

interface ItemStoreController {
    fun onItemStoreClick(id: Long)
    fun onItemStoreLongClick(store: Store):Boolean
    fun onCheckBoxFavoriteClick(store: Store)
}