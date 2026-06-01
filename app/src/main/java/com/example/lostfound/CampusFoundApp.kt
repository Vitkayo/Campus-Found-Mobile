package com.example.lostfound

import android.app.Application
import com.example.lostfound.util.ThemeHelper

class CampusFoundApp : Application() {
    override fun onCreate() {
        super.onCreate()
        ThemeHelper.applySavedTheme(this)
    }
}
