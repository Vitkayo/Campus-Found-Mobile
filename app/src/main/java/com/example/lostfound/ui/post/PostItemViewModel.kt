package com.example.lostfound.ui.post

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.content.Context
import com.example.lostfound.R
import com.example.lostfound.data.ItemRepository
import com.example.lostfound.model.Item
import com.example.lostfound.service.ItemImageUploadService
import com.example.lostfound.service.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

data class PostItemUiState(
    val isSubmitting: Boolean = false,
    val submitSuccess: Boolean? = null,
    val error: String? = null,
    val title: String = "",
    val category: String = "",
    val description: String = "",
    val location: String = "",
    val contact: String = "",
    val date: String = "",
    val status: String = "lost",
    val imageUrl: String = ""
)

@HiltViewModel
class PostItemViewModel @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val repository: ItemRepository,
    private val imageUploadService: ItemImageUploadService,
    private val sessionManager: SessionManager,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(PostItemUiState())
    val uiState: StateFlow<PostItemUiState> = _uiState.asStateFlow()

    private var createdItem: Item? = null

    init {
        _uiState.update { it.copy(
            title = savedStateHandle["draft_title"] ?: "",
            category = savedStateHandle["draft_category"] ?: "",
            description = savedStateHandle["draft_description"] ?: "",
            location = savedStateHandle["draft_location"] ?: "",
            contact = savedStateHandle["draft_contact"] ?: "",
            date = savedStateHandle["draft_date"] ?: "",
            status = savedStateHandle["draft_status"] ?: "lost",
            imageUrl = savedStateHandle["draft_image_url"] ?: ""
        )}
    }

    fun saveDraft(
        titleValue: String,
        categoryValue: String,
        descriptionValue: String,
        locationValue: String,
        contactValue: String,
        dateValue: String,
        statusValue: String,
        imageUrlValue: String
    ) {
        savedStateHandle["draft_title"] = titleValue
        savedStateHandle["draft_category"] = categoryValue
        savedStateHandle["draft_description"] = descriptionValue
        savedStateHandle["draft_location"] = locationValue
        savedStateHandle["draft_contact"] = contactValue
        savedStateHandle["draft_date"] = dateValue
        savedStateHandle["draft_status"] = statusValue
        savedStateHandle["draft_image_url"] = imageUrlValue

        _uiState.update { it.copy(
            title = titleValue,
            category = categoryValue,
            description = descriptionValue,
            location = locationValue,
            contact = contactValue,
            date = dateValue,
            status = statusValue,
            imageUrl = imageUrlValue
        )}
    }

    fun submitItem(
        titleValue: String,
        categoryValue: String,
        descriptionValue: String,
        locationValue: String,
        contactValue: String,
        dateValue: String,
        statusValue: String,
        imagePathsOrJoined: String
    ) {
        if (titleValue.isBlank() || categoryValue.isBlank()) {
            _uiState.update { it.copy(error = "Title and Category are required") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, error = null) }
            try {
                val imageUrl = imageUploadService.uploadMultiple(imagePathsOrJoined)
                val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
                val newItem = Item(
                    title = titleValue.trim(),
                    category = categoryValue.trim(),
                    description = descriptionValue.trim(),
                    location = locationValue.trim(),
                    contactInfo = contactValue.trim(),
                    date = dateValue.trim().ifBlank { today },
                    status = statusValue,
                    imageUrl = imageUrl,
                    reporterName = sessionManager.getUserName(),
                    createdAt = System.currentTimeMillis().toString()
                )
                createdItem = repository.createItem(newItem)
                _uiState.update { it.copy(submitSuccess = true, isSubmitting = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    error = mapSubmitError(e),
                    submitSuccess = false,
                    isSubmitting = false
                )}
            }
        }
    }

    private fun mapSubmitError(e: Exception): String {
        val message = e.message.orEmpty()
        return when {
            message.contains("413") ->
                appContext.getString(R.string.error_photo_too_large)
            message.contains(appContext.getString(R.string.error_photo_upload_failed)) ->
                appContext.getString(R.string.error_photo_upload_failed)
            message.contains("Firebase", ignoreCase = true) ||
                message.contains("upload", ignoreCase = true) ||
                message.contains("Permission denied", ignoreCase = true) ->
                appContext.getString(R.string.error_firebase_upload)
            message.isNotBlank() -> message
            else -> appContext.getString(R.string.error_post_failed)
        }
    }

    fun takeCreatedItem(): Item? {
        val item = createdItem
        createdItem = null
        return item
    }

    fun clearSubmitSuccess() {
        _uiState.update { it.copy(submitSuccess = null) }
    }

    fun clearDraft() {
        saveDraft("", "", "", "", "", "", "lost", "")
    }
}
