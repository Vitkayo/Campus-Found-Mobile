package com.example.lostfound.ui.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.lostfound.model.Item
import com.example.lostfound.service.ItemService
import com.example.lostfound.service.SessionManager
import com.example.lostfound.util.CredentialUtils
import com.example.lostfound.util.ItemSort

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val itemService = ItemService(application)
    private val sessionManager = SessionManager(application)

    private val _myItems = MutableLiveData<List<Item>>(emptyList())
    val myItems: LiveData<List<Item>> = _myItems

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _userName = MutableLiveData(sessionManager.getUserName())
    val userName: LiveData<String> = _userName

    private val _email = MutableLiveData(sessionManager.getEmail())
    val email: LiveData<String> = _email

    private val _studentId = MutableLiveData(sessionManager.getStudentId())
    val studentId: LiveData<String> = _studentId

    private val _phone = MutableLiveData(sessionManager.getPhone())
    val phone: LiveData<String> = _phone

    private val _totalPosts = MutableLiveData(0)
    val totalPosts: LiveData<Int> = _totalPosts

    private val _lostCount = MutableLiveData(0)
    val lostCount: LiveData<Int> = _lostCount

    private val _foundCount = MutableLiveData(0)
    val foundCount: LiveData<Int> = _foundCount

    fun refreshUserInfo() {
        _userName.value = sessionManager.getUserName()
        _email.value = sessionManager.getEmail()
        _studentId.value = sessionManager.getStudentId()
        _phone.value = sessionManager.getPhone()
    }

    sealed class ProfileUpdateResult {
        data object Success : ProfileUpdateResult()
        data object NameRequired : ProfileUpdateResult()
        data object InvalidEmail : ProfileUpdateResult()
        data object InvalidPhone : ProfileUpdateResult()
        data object PasswordTooShort : ProfileUpdateResult()
        data object PasswordMismatch : ProfileUpdateResult()
    }

    fun updateAccount(
        name: String,
        email: String,
        phone: String,
        newPassword: String,
        confirmPassword: String
    ): ProfileUpdateResult {
        if (name.isBlank()) return ProfileUpdateResult.NameRequired
        if (!CredentialUtils.isValidEmail(email)) return ProfileUpdateResult.InvalidEmail
        if (!CredentialUtils.isValidPhone(phone)) return ProfileUpdateResult.InvalidPhone

        val passwordChanging = newPassword.isNotBlank() || confirmPassword.isNotBlank()
        if (passwordChanging) {
            if (newPassword.length < 6) return ProfileUpdateResult.PasswordTooShort
            if (newPassword != confirmPassword) return ProfileUpdateResult.PasswordMismatch
        }

        if (!sessionManager.updateAccount(name, email, phone, newPassword, confirmPassword)) {
            return ProfileUpdateResult.InvalidEmail
        }

        refreshUserInfo()
        return ProfileUpdateResult.Success
    }

    fun loadMyItemsIfNeeded() {
        if (!_myItems.value.isNullOrEmpty() || _isLoading.value == true) return
        loadMyItems()
    }

    fun refreshMyItems() {
        loadMyItems()
    }

    fun loadMyItems() {
        _isLoading.value = true
        val currentUser = sessionManager.getUserName().lowercase()

        itemService.getAllItems(object : ItemService.ItemCallback<List<Item>> {
            override fun onSuccess(data: List<Item>) {
                val mine = data.filter {
                    it.reporterName?.lowercase() == currentUser ||
                        it.reporterName?.lowercase() ==
                        sessionManager.getEmail().substringBefore("@").lowercase()
                }
                _myItems.postValue(ItemSort.newestFirst(mine))
                _totalPosts.postValue(mine.size)
                _lostCount.postValue(mine.count { it.status.equals("lost", ignoreCase = true) })
                _foundCount.postValue(mine.count { it.status.equals("found", ignoreCase = true) })
                _isLoading.postValue(false)
            }

            override fun onError(message: String) {
                _myItems.postValue(emptyList())
                _isLoading.postValue(false)
            }
        })
    }

    fun deleteItem(itemId: String, onComplete: (Boolean) -> Unit) {
        itemService.deleteItem(itemId, object : ItemService.ItemCallback<Item> {
            override fun onSuccess(data: Item) {
                loadMyItems()
                onComplete(true)
            }

            override fun onError(message: String) {
                onComplete(false)
            }
        })
    }

    fun logout() {
        sessionManager.clearSession()
    }
}
