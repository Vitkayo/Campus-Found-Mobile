package com.example.lostfound.util

object ImageUrls {

    private const val DELIMITER = "|"
    const val MAX_PHOTOS = 5

    fun join(urls: List<String>): String {
        return urls.filter { it.isNotBlank() }.joinToString(DELIMITER)
    }

    fun split(value: String?): List<String> {
        if (value.isNullOrBlank()) return emptyList()
        if (!value.contains(DELIMITER)) return listOf(value)
        return value.split(DELIMITER).map { it.trim() }.filter { it.isNotBlank() }
    }

    fun primary(value: String?): String? = split(value).firstOrNull()
}
