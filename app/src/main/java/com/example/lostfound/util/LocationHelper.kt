package com.example.lostfound.util

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import java.util.Locale

object LocationHelper {

    @SuppressLint("MissingPermission")
    fun fetchCurrentLocation(
        context: Context,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        val manager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val providers = listOf(
            LocationManager.GPS_PROVIDER,
            LocationManager.NETWORK_PROVIDER
        )

        var best: Location? = null
        for (provider in providers) {
            if (!manager.isProviderEnabled(provider)) continue
            val location = manager.getLastKnownLocation(provider) ?: continue
            if (best == null || location.accuracy < best.accuracy) {
                best = location
            }
        }

        if (best == null) {
            onError("Location unavailable. Enable GPS or enter location manually.")
            return
        }

        val formatted = formatLocation(context, best)
        onSuccess(formatted)
    }

    private fun formatLocation(context: Context, location: Location): String {
        if (!Geocoder.isPresent()) {
            return String.format(
                Locale.getDefault(),
                "%.5f, %.5f",
                location.latitude,
                location.longitude
            )
        }

        return try {
            @Suppress("DEPRECATION")
            val addresses = Geocoder(context, Locale.getDefault()).getFromLocation(
                location.latitude,
                location.longitude,
                1
            )
            val line = addresses?.firstOrNull()?.getAddressLine(0)
            if (!line.isNullOrBlank()) line else formatCoordinates(location)
        } catch (_: Exception) {
            formatCoordinates(location)
        }
    }

    private fun formatCoordinates(location: Location): String {
        return String.format(
            Locale.getDefault(),
            "%.5f, %.5f",
            location.latitude,
            location.longitude
        )
    }

    fun formatDisplayLocation(location: String?): String {
        if (location.isNullOrBlank()) return ""
        return location
            .replace(Regex(",\\s*RUPP\\b", RegexOption.IGNORE_CASE), "")
            .replace(Regex("\\bRUPP\\s*,\\s*", RegexOption.IGNORE_CASE), "")
            .replace(Regex("^RUPP\\s+", RegexOption.IGNORE_CASE), "")
            .replace(Regex("\\s+RUPP$", RegexOption.IGNORE_CASE), "")
            .trim()
    }
}
