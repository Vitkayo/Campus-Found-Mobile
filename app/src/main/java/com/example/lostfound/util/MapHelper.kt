package com.example.lostfound.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.net.toUri

object MapHelper {

    fun openLocation(context: Context, location: String): Boolean {
        val query = location.trim()
        if (query.isBlank()) return false

        val encoded = Uri.encode(query)
        val geoIntent = Intent(Intent.ACTION_VIEW, "geo:0,0?q=$encoded".toUri())
        val mapsIntent = Intent(
            Intent.ACTION_VIEW,
            "https://www.google.com/maps/search/?api=1&query=$encoded".toUri()
        )

        val candidates = listOf(geoIntent, mapsIntent)
        for (intent in candidates) {
            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
                return true
            }
        }
        return false
    }

    fun looksLikeCoordinates(location: String): Boolean {
        return COORD_REGEX.containsMatchIn(location.trim())
    }

    private val COORD_REGEX = Regex("""-?\d+\.\d+\s*,\s*-?\d+\.\d+""")
}
