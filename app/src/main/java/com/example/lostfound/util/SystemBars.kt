package com.example.lostfound.util

import android.content.res.Configuration
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.updatePadding
import com.example.lostfound.R

object SystemBars {

    fun apply(
        activity: AppCompatActivity,
        root: View,
        bottomInsetTarget: View? = null
    ) {
        val window = activity.window
        val isDark = (activity.resources.configuration.uiMode and
            Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES

        WindowCompat.setDecorFitsSystemWindows(window, false)

        window.statusBarColor = activity.getColor(R.color.surface)
        window.navigationBarColor = activity.getColor(R.color.surface_container)

        WindowInsetsControllerCompat(window, root).apply {
            isAppearanceLightStatusBars = !isDark
            isAppearanceLightNavigationBars = !isDark
        }

        ViewCompat.setOnApplyWindowInsetsListener(root) { view, windowInsets ->
            val systemBars: Insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(
                left = systemBars.left,
                top = systemBars.top,
                right = systemBars.right
            )
            bottomInsetTarget?.updatePadding(bottom = systemBars.bottom)
            windowInsets
        }

        ViewCompat.requestApplyInsets(root)
    }
}
