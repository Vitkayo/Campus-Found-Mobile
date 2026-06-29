package com.example.lostfound.data

import com.example.lostfound.api.ApiService
import com.example.lostfound.model.User
import com.example.lostfound.util.CredentialUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

class AuthException(message: String) : Exception(message)

@Singleton
class UserRepository @Inject constructor(
    private val apiService: ApiService
) {

    suspend fun login(identifier: String, password: String): User = withContext(Dispatchers.IO) {
        val trimmed = identifier.trim()
        val users = if (CredentialUtils.isValidEmail(trimmed)) {
            apiService.getUsers(email = trimmed)
        } else {
            apiService.getUsers().filter { user ->
                !user.phone.isNullOrBlank() && CredentialUtils.phonesMatch(trimmed, user.phone!!)
            }
        }

        users.firstOrNull { it.password == password }
            ?: throw AuthException("Invalid email/phone or password")
    }

    suspend fun register(
        username: String,
        email: String,
        phone: String,
        password: String
    ): User = withContext(Dispatchers.IO) {
        val normalizedEmail = email.trim()
        val existing = apiService.getUsers(email = normalizedEmail)
        if (existing.isNotEmpty()) {
            throw AuthException("An account with this email already exists")
        }

        apiService.createUser(
            User(
                name = username.trim(),
                email = normalizedEmail,
                password = password,
                phone = CredentialUtils.normalizePhone(phone)
            )
        )
    }

    suspend fun updateUser(
        userId: String,
        name: String,
        email: String,
        phone: String,
        password: String
    ): User = withContext(Dispatchers.IO) {
        apiService.updateUser(
            userId,
            User(
                name = name.trim(),
                email = email.trim(),
                phone = CredentialUtils.normalizePhone(phone),
                password = password
            )
        )
    }

    suspend fun resetPassword(
        email: String,
        newPassword: String,
        confirmPassword: String
    ): User = withContext(Dispatchers.IO) {
        val normalizedEmail = email.trim()
        if (!CredentialUtils.isValidEmail(normalizedEmail)) {
            throw AuthException("Enter a valid email address")
        }
        if (newPassword.length < 6) {
            throw AuthException("Password must be at least 6 characters")
        }
        if (newPassword != confirmPassword) {
            throw AuthException("Passwords do not match")
        }

        val user = apiService.getUsers(email = normalizedEmail).firstOrNull()
            ?: throw AuthException("No account found with this email")

        val userId = user.id ?: throw AuthException("Unable to reset password")

        apiService.updateUser(
            userId,
            User(
                name = user.name,
                email = user.email,
                phone = user.phone,
                password = newPassword
            )
        )
    }
}
