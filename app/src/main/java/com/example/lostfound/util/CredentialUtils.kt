package com.example.lostfound.util

import android.util.Patterns

object CredentialUtils {

    fun isValidEmail(value: String): Boolean =
        Patterns.EMAIL_ADDRESS.matcher(value.trim()).matches()

    fun isValidPhone(value: String): Boolean {
        val digits = value.filter { it.isDigit() }
        return digits.length in 8..15
    }

    fun isValidLoginIdentifier(value: String): Boolean {
        val trimmed = value.trim()
        return isValidEmail(trimmed) || isValidPhone(trimmed)
    }

    fun normalizePhone(value: String): String {
        val digits = value.filter { it.isDigit() }
        return when {
            digits.startsWith("855") && digits.length >= 11 -> digits
            digits.startsWith("0") && digits.length >= 9 -> "855" + digits.drop(1)
            else -> digits
        }
    }

    fun phonesMatch(first: String, second: String): Boolean =
        normalizePhone(first) == normalizePhone(second)

    fun identifierMatchesLogin(identifier: String, email: String, phone: String): Boolean {
        val trimmed = identifier.trim()
        if (trimmed.equals(email, ignoreCase = true)) return true
        if (phone.isNotBlank() && isValidPhone(trimmed)) {
            return phonesMatch(trimmed, phone)
        }
        return false
    }
}
