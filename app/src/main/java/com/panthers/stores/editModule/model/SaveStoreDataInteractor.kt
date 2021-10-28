package com.panthers.stores.editModule.model

import com.panthers.stores.App
import com.panthers.stores.database.entities.Store
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SaveStoreDataInteractor {

    suspend fun getStoreByID(id: Long): Store {
        return withContext(Dispatchers.IO) { App.db.storeDAO().getStoreByID(id) }
    }

    suspend fun saveNewStore(newStore: Store) {
        withContext(Dispatchers.IO) { App.db.storeDAO().addStore(newStore) }
    }

    suspend fun editStore(store: Store) {
        withContext(Dispatchers.IO) { App.db.storeDAO().editStore(store) }
    }

}