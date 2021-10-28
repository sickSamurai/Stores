package com.panthers.stores.editModule.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.panthers.stores.database.entities.Store
import com.panthers.stores.editModule.model.SaveStoreDataInteractor
import kotlinx.coroutines.launch

class SaveStoreDataViewModel : ViewModel() {
    private val interactor by lazy { SaveStoreDataInteractor() }
    private var selectedStoreLiveData: MutableLiveData<Store?> = MutableLiveData()
    private val showFABLiveData: MutableLiveData<Boolean> = MutableLiveData()

    fun getSelectedStoreLiveData(): MutableLiveData<Store?> = selectedStoreLiveData

    fun retrieveSelectedStoreFromDB(id: Long) {
        viewModelScope.launch { selectedStoreLiveData.postValue(interactor.getStoreByID(id)) }
    }

    fun setSelectedStoreToNull() {
        viewModelScope.launch { selectedStoreLiveData.postValue(null) }
    }

    fun getFABVisibilityLiveData(): LiveData<Boolean> = showFABLiveData

    fun showFAB() {
        showFABLiveData.value = true
    }

    fun hideFAB() {
        showFABLiveData.value = false
    }

    fun saveNewStore(store: Store) {
        viewModelScope.launch {
            interactor.saveNewStore(store)
        }
    }

    fun editStore(store: Store) {
        viewModelScope.launch {
            interactor.editStore(store)
        }
    }

}