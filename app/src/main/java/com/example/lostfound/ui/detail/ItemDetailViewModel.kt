package com.example.lostfound.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lostfound.data.ItemRepository
import com.example.lostfound.model.Item
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ItemDetailUiState(
    val item: Item? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val claimSuccess: Boolean = false
)

@HiltViewModel
class ItemDetailViewModel @Inject constructor(
    private val repository: ItemRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ItemDetailUiState())
    val uiState: StateFlow<ItemDetailUiState> = _uiState.asStateFlow()

    fun loadItem(id: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, claimSuccess = false) }
            try {
                val item = repository.getItemById(id)
                _uiState.update { it.copy(item = item, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    error = e.message ?: "Failed to load item",
                    isLoading = false
                )}
            }
        }
    }

    fun updateItemStatus(newStatus: String) {
        viewModelScope.launch {
            val current = _uiState.value.item ?: return@launch
            val id = current.id ?: return@launch
            _uiState.update { it.copy(isLoading = true, error = null, claimSuccess = false) }
            try {
                val updated = repository.updateItem(id, current.copy(status = newStatus))
                _uiState.update { it.copy(item = updated, isLoading = false, claimSuccess = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    error = e.message ?: "Update failed",
                    isLoading = false
                )}
            }
        }
    }

    fun clearClaimSuccess() {
        _uiState.update { it.copy(claimSuccess = false) }
    }
}
