package com.example.lostfound.ui.home

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lostfound.data.ItemRepository
import com.example.lostfound.model.Item
import com.example.lostfound.util.ItemSort
import com.example.lostfound.util.StatusUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: ItemRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var scrollState: Parcelable? = savedStateHandle.get<Parcelable>("recycler_scroll_state")

    init {
        _uiState.update { state ->
            state.copy(
                searchQuery = savedStateHandle["search_query"] ?: "",
                selectedStatusFilter = savedStateHandle["selected_status_filter"] ?: "All",
                selectedCategory = savedStateHandle["selected_category"] ?: ""
            )
        }

        viewModelScope.launch {
            combine(
                repository.itemsFlow,
                _uiState.map { it.searchQuery }.distinctUntilChanged().debounce(300).onStart { emit(_uiState.value.searchQuery) },
                _uiState.map { it.selectedStatusFilter }.distinctUntilChanged(),
                _uiState.map { it.selectedCategory }.distinctUntilChanged()
            ) { items, query, statusFilter, categoryFilter ->
                ItemSort.newestFirst(
                    items.filter { item ->
                        StatusUtils.matchesSearch(item, query) &&
                            StatusUtils.matchesHomeFilters(item, statusFilter, categoryFilter)
                    }
                )
            }.flowOn(Dispatchers.Default)
                .collectLatest { filteredItems ->
                    _uiState.update { it.copy(items = filteredItems, isLoading = false) }
                }
        }

        refreshItems()
    }

    fun loadItems() {
        refreshItems()
    }

    fun refreshItems() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true, error = null) }
            try {
                repository.refreshItems()
                _uiState.update { it.copy(isRefreshing = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    isRefreshing = false,
                    error = e.message ?: "Refresh failed"
                )}
            }
        }
    }

    fun refreshAfterNewPost() {
        _uiState.update { state ->
            state.copy(
                selectedStatusFilter = "All",
                selectedCategory = "",
                searchQuery = ""
            )
        }
        savedStateHandle["selected_status_filter"] = "All"
        savedStateHandle["selected_category"] = ""
        savedStateHandle["search_query"] = ""
        clearScrollState()
        refreshItems()
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        savedStateHandle["search_query"] = query
    }

    fun setSelectedStatusFilter(filter: String) {
        _uiState.update { it.copy(selectedStatusFilter = filter) }
        savedStateHandle["selected_status_filter"] = filter
    }

    fun setSelectedCategory(category: String) {
        _uiState.update { it.copy(selectedCategory = category) }
        savedStateHandle["selected_category"] = category
    }

    fun saveScrollState(state: Parcelable?) {
        scrollState = state
        savedStateHandle["recycler_scroll_state"] = state
    }

    fun getScrollState(): Parcelable? = scrollState

    fun clearScrollState() {
        scrollState = null
        savedStateHandle.remove<Parcelable>("recycler_scroll_state")
    }
}
