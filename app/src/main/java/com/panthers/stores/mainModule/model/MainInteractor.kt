package com.panthers.stores.mainModule.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import com.panthers.stores.App
import com.panthers.stores.database.entities.Store
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainInteractor {

    val stores: LiveData<MutableList<Store>> = liveData {
        emitSource(App.db.storeDAO().getAll().map { stores -> stores.sortedBy { it.name } as MutableList<Store> })
    }

    suspend fun deleteStore(store: Store) {
        App.db.storeDAO().deleteStore(store)
    }

    suspend fun toggleStoreFavorability(store: Store) {
        store.apply { isFavorite = !isFavorite }
        App.db.storeDAO().editStore(store)
    }
}