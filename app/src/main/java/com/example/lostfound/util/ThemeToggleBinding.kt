package com.example.lostfound.util

import android.content.res.ColorStateList
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import com.example.lostfound.R
import com.example.lostfound.service.SessionManager

object ThemeToggleBinding {

    fun bind(button: ImageButton, activity: AppCompatActivity) {
        button.scaleType = ImageView.ScaleType.FIT_CENTER
        refreshIcon(button, activity)
        button.setOnClickListener {
            val sessionManager = SessionManager(activity)
            ThemeHelper.setDarkMode(activity, !sessionManager.isDarkMode())
        }
    }

    fun refreshIcon(button: ImageButton, activity: AppCompatActivity) {
        val isDark = SessionManager(activity).isDarkMode()
        if (isDark) {
            button.setImageResource(R.drawable.ic_light_mode)
            button.contentDescription = activity.getString(R.string.switch_to_light_mode)
        } else {
            button.setImageResource(R.drawable.ic_dark_mode)
            button.contentDescription = activity.getString(R.string.switch_to_dark_mode)
        }
        val tint = ContextCompat.getColor(activity, R.color.primary)
        ImageViewCompat.setImageTintList(button, ColorStateList.valueOf(tint))
    }
}
