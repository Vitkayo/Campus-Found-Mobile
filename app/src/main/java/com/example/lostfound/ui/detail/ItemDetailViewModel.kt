package com.example.lostfound.ui.detail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.lostfound.model.Item
import com.example.lostfound.service.ItemService

class ItemDetailViewModel(application: Application) : AndroidViewModel(application) {

    private val itemService = ItemService(application)

    private val _item = MutableLiveData<Item?>()
    val item: LiveData<Item?> = _item

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun loadItem(itemId: String) {
        _isLoading.value = true
        itemService.getItemById(itemId, object : ItemService.ItemCallback<Item> {
            override fun onSuccess(data: Item) {
                _item.postValue(data)
                _isLoading.postValue(false)
            }

            override fun onError(message: String) {
                _error.postValue(message)
                _isLoading.postValue(false)
            }
        })
    }
}
