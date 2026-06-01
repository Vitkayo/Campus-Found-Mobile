package com.example.lostfound.ui.home

import android.app.Application
import android.os.Parcelable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.example.lostfound.model.Item
import com.example.lostfound.service.ItemService
import com.example.lostfound.util.ItemSort
import com.example.lostfound.util.StatusUtils

class HomeViewModel(
    application: Application,
    private val savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    private val itemService = ItemService(application)

    private val _items = MutableLiveData<List<Item>>(emptyList())
    val items: LiveData<List<Item>> = _items

    private val _filteredItems = MutableLiveData<List<Item>>(emptyList())
    val filteredItems: LiveData<List<Item>> = _filteredItems

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isRefreshing = MutableLiveData(false)
    val isRefreshing: LiveData<Boolean> = _isRefreshing

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    val searchQuery = savedStateHandle.getLiveData("search_query", "")
    val selectedFilter = savedStateHandle.getLiveData("selected_filter", "All")

    private var scrollState: Parcelable? =
        savedStateHandle.get<Parcelable>("recycler_scroll_state")

    fun saveScrollState(state: Parcelable?) {
        scrollState = state
        savedStateHandle["recycler_scroll_state"] = state
    }

    fun getScrollState(): Parcelable? = scrollState

    fun loadItemsIfNeeded() {
        if (!_items.value.isNullOrEmpty() || _isLoading.value == true) return
        loadItems()
    }

    fun loadItems() {
        if (_isLoading.value == true) return
        _error.value = null

        if (_items.value.isNullOrEmpty()) {
            _isLoading.value = true
            itemService.getCachedItemsAsync(object : ItemService.ItemCallback<List<Item>> {
                override fun onSuccess(data: List<Item>) {
                    if (data.isNotEmpty() && _items.value.isNullOrEmpty()) {
                        _items.postValue(data)
                        applyFilters()
                        _isLoading.postValue(false)
                    }
                    fetchItems()
                }

                override fun onError(message: String) {
                    fetchItems()
                }
            })
        } else {
            fetchItems()
        }
    }

    fun refreshItems() {
        if (_isRefreshing.value == true) return
        _isRefreshing.value = true
        _error.value = null
        fetchItems()
    }

    private fun fetchItems() {
        if (_items.value.isNullOrEmpty() && _isLoading.value != true) {
            _isLoading.postValue(true)
        }
        itemService.getAllItems(object : ItemService.ItemCallback<List<Item>> {
            override fun onSuccess(data: List<Item>) {
                _items.postValue(data)
                applyFilters()
                _isLoading.postValue(false)
                _isRefreshing.postValue(false)
            }

            override fun onError(message: String) {
                _error.postValue(message)
                _isLoading.postValue(false)
                _isRefreshing.postValue(false)
            }
        })
    }

    fun setSearchQuery(query: String) {
        savedStateHandle["search_query"] = query
        applyFilters()
    }

    fun setSelectedFilter(filter: String) {
        savedStateHandle["selected_filter"] = filter
        applyFilters()
    }

    private fun applyFilters() {
        val allItems = _items.value.orEmpty()
        val query = searchQuery.value.orEmpty()
        val filter = selectedFilter.value ?: "All"

        val filtered = ItemSort.newestFirst(
            allItems.filter { item ->
                StatusUtils.matchesSearch(item, query) && StatusUtils.matchesFilter(item, filter)
            }
        )
        _filteredItems.postValue(filtered)
    }

    fun clearScrollState() {
        scrollState = null
        savedStateHandle.remove<Parcelable>("recycler_scroll_state")
    }
}
