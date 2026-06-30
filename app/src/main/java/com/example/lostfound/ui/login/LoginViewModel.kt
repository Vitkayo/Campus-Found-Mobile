package com.example.lostfound.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lostfound.data.AuthException
import com.example.lostfound.data.UserRepository
import com.example.lostfound.service.SessionManager
import com.example.lostfound.util.CredentialUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null,
    val loginSucceeded: Boolean = false
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun login(identifier: String, password: String, rememberMe: Boolean) {
        if (identifier.isBlank()) {
            _uiState.update { it.copy(error = "identifier_required") }
            return
        }
        if (!CredentialUtils.isValidLoginIdentifier(identifier)) {
            _uiState.update { it.copy(error = "invalid_identifier") }
            return
        }
        if (password.length < 6) {
            _uiState.update { it.copy(error = "password_too_short") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, successMessage = null) }
            try {
                val user = userRepository.login(identifier, password)
                sessionManager.saveSession(
                    userId = user.id.orEmpty(),
                    email = user.email.orEmpty(),
                    name = user.name.orEmpty(),
                    phone = user.phone.orEmpty(),
                    studentId = "",
                    rememberMe = rememberMe,
                    password = password
                )
                _uiState.update { it.copy(isLoading = false, loginSucceeded = true) }
            } catch (e: AuthException) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "network_error"
                    )
                }
            }
        }
    }

    fun register(username: String, email: String, phone: String, password: String) {
        if (username.isBlank() ||
            !CredentialUtils.isValidEmail(email) ||
            !CredentialUtils.isValidPhone(phone) ||
            password.length < 6
        ) {
            _uiState.update { it.copy(error = "register_validation") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, successMessage = null) }
            try {
                userRepository.register(username, email, phone, password)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        successMessage = "registration_success"
                    )
                }
            } catch (e: AuthException) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "network_error"
                    )
                }
            }
        }
    }

    fun resetPassword(email: String, newPassword: String, confirmPassword: String) {
        if (!CredentialUtils.isValidEmail(email)) {
            _uiState.update { it.copy(error = "forgot_invalid_email") }
            return
        }
        if (newPassword.length < 6) {
            _uiState.update { it.copy(error = "password_too_short") }
            return
        }
        if (newPassword != confirmPassword) {
            _uiState.update { it.copy(error = "forgot_password_mismatch") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, successMessage = null) }
            try {
                userRepository.resetPassword(email, newPassword, confirmPassword)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        successMessage = "password_reset_success"
                    )
                }
            } catch (e: AuthException) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "network_error"
                    )
                }
            }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(error = null, successMessage = null) }
    }

    fun consumeLoginSuccess() {
        _uiState.update { it.copy(loginSucceeded = false) }
    }
}
