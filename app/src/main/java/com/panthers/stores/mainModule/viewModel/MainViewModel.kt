package com.panthers.stores.mainModule.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.panthers.stores.database.entities.Store
import com.panthers.stores.mainModule.model.MainInteractor
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private var interactor: MainInteractor = MainInteractor()
    private var storesLiveData: LiveData<MutableList<Store>> = interactor.stores
    private var visibiltyOfProgressBarLiveData = MutableLiveData<Boolean>()

    fun getStoresLiveData(): LiveData<MutableList<Store>> = storesLiveData


    fun getVisibiltyOfProgressBarLiveData(): LiveData<Boolean> {
        return visibiltyOfProgressBarLiveData
    }

    fun changeProgressBarVisibility(showProgress: Boolean) {
        this.visibiltyOfProgressBarLiveData.postValue(showProgress)
    }

    fun toggleStoreFavorability(store: Store) {
        viewModelScope.launch {
            interactor.toggleStoreFavorability(store)
        }
    }

    fun deleteStore(store: Store) {
        viewModelScope.launch {
            interactor.deleteStore(store)
        }
    }
}