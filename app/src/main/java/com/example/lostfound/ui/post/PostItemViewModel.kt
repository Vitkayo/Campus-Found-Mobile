package com.example.lostfound.ui.post

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.example.lostfound.model.Item
import com.example.lostfound.service.ItemImageUploadService
import com.example.lostfound.service.ItemService
import com.example.lostfound.service.SessionManager
import com.example.lostfound.util.DateUtils

class PostItemViewModel(
    application: Application,
    private val savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    private val itemService = ItemService(application)
    private val imageUploadService = ItemImageUploadService(application)
    private val sessionManager = SessionManager(application)

    val title = savedStateHandle.getLiveData("draft_title", "")
    val category = savedStateHandle.getLiveData("draft_category", "")
    val description = savedStateHandle.getLiveData("draft_description", "")
    val location = savedStateHandle.getLiveData("draft_location", "")
    val contact = savedStateHandle.getLiveData("draft_contact", "")
    val date = savedStateHandle.getLiveData("draft_date", DateUtils.todayIsoDate())
    val status = savedStateHandle.getLiveData("draft_status", "lost")
    val imageUrl = savedStateHandle.getLiveData("draft_image_url", "")

    private val _isSubmitting = MutableLiveData(false)
    val isSubmitting: LiveData<Boolean> = _isSubmitting

    private val _submitSuccess = MutableLiveData(false)
    val submitSuccess: LiveData<Boolean> = _submitSuccess

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

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
    }

    fun submitItem(
        titleValue: String,
        categoryValue: String,
        descriptionValue: String,
        locationValue: String,
        contactValue: String,
        dateValue: String,
        statusValue: String,
        imagePathOrUri: String
    ) {
        if (titleValue.isBlank()) {
            _error.value = "Please enter an item name"
            return
        }
        if (categoryValue.isBlank()) {
            _error.value = "Please choose a category"
            return
        }

        val app = getApplication<Application>()
        val resolvedDescription = descriptionValue.trim()
        val resolvedLocation = locationValue.trim().ifBlank {
            app.getString(com.example.lostfound.R.string.default_location_rupp)
        }
        val resolvedContact = contactValue.trim().ifBlank { sessionManager.getDefaultContact() }
        val resolvedDate = dateValue.trim().ifBlank { DateUtils.todayIsoDate() }

        _isSubmitting.value = true
        _error.value = null

        imageUploadService.uploadForApi(
            localPathOrUri = imagePathOrUri,
            onSuccess = { apiImageUrl ->
                createItemOnApi(
                    titleValue = titleValue,
                    categoryValue = categoryValue,
                    descriptionValue = resolvedDescription,
                    locationValue = resolvedLocation,
                    contactValue = resolvedContact,
                    dateValue = resolvedDate,
                    statusValue = statusValue,
                    imageUrl = apiImageUrl
                )
            },
            onError = { message ->
                _isSubmitting.postValue(false)
                _error.postValue(message)
            }
        )
    }

    private fun createItemOnApi(
        titleValue: String,
        categoryValue: String,
        descriptionValue: String,
        locationValue: String,
        contactValue: String,
        dateValue: String,
        statusValue: String,
        imageUrl: String,
        photoSkippedDueToSize: Boolean = false
    ) {
        val item = Item(
            title = titleValue.trim(),
            category = categoryValue.trim(),
            description = descriptionValue.trim(),
            location = locationValue.trim(),
            contactInfo = contactValue.trim(),
            status = statusValue.lowercase(),
            imageUrl = imageUrl,
            date = dateValue,
            createdAt = System.currentTimeMillis().toString(),
            reporterName = sessionManager.getUserName()
        )

        itemService.createItem(item, object : ItemService.ItemCallback<Item> {
            override fun onSuccess(data: Item) {
                clearDraft()
                _isSubmitting.postValue(false)
                _submitSuccess.postValue(true)
                if (photoSkippedDueToSize) {
                    _error.postValue(
                        getApplication<Application>().getString(
                            com.example.lostfound.R.string.photo_skipped_too_large
                        )
                    )
                }
            }

            override fun onError(message: String) {
                if (message.contains("413") && imageUrl.isNotBlank() && !photoSkippedDueToSize) {
                    createItemOnApi(
                        titleValue = titleValue,
                        categoryValue = categoryValue,
                        descriptionValue = descriptionValue,
                        locationValue = locationValue,
                        contactValue = contactValue,
                        dateValue = dateValue,
                        statusValue = statusValue,
                        imageUrl = "",
                        photoSkippedDueToSize = true
                    )
                } else {
                    _isSubmitting.postValue(false)
                    _error.postValue(message)
                }
            }
        })
    }

    fun clearSubmitSuccess() {
        _submitSuccess.value = false
    }

    fun clearDraft() {
        savedStateHandle["draft_title"] = ""
        savedStateHandle["draft_category"] = ""
        savedStateHandle["draft_description"] = ""
        savedStateHandle["draft_location"] = ""
        savedStateHandle["draft_contact"] = ""
        savedStateHandle["draft_date"] = DateUtils.todayIsoDate()
        savedStateHandle["draft_status"] = "lost"
        savedStateHandle["draft_image_url"] = ""
    }
}
