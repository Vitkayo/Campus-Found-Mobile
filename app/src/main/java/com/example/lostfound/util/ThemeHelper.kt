package com.example.lostfound.util

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.example.lostfound.service.SessionManager

object ThemeHelper {

    fun applySavedTheme(context: Context) {
        AppCompatDelegate.setDefaultNightMode(
            if (SessionManager(context).isDarkMode()) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

    fun setDarkMode(activity: Activity, enabled: Boolean) {
        SessionManager(activity).setDarkMode(enabled)
        val mode =
            if (enabled) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        AppCompatDelegate.setDefaultNightMode(mode)
        (activity as? androidx.appcompat.app.AppCompatActivity)?.delegate?.localNightMode = mode
        activity.recreate()
    }
}
