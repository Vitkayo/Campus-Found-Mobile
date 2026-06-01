package com.example.lostfound.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.TextView
import androidx.core.net.toUri
import com.google.android.material.dialog.MaterialAlertDialogBuilder

object ContactLinkHelper {

    data class ContactAction(
        val label: String,
        val intent: Intent
    )

    fun parseActions(contactText: String): List<ContactAction> {
        val actions = mutableListOf<ContactAction>()
        val text = contactText.trim()
        if (text.isBlank()) return emptyList()

        PHONE_REGEX.findAll(text).forEach { match ->
            val digits = match.value.filter { it.isDigit() || it == '+' }
            if (digits.length >= 7) {
                actions.add(
                    ContactAction(
                        label = "Call ${match.value.trim()}",
                        intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$digits"))
                    )
                )
            }
        }

        EMAIL_REGEX.findAll(text).forEach { match ->
            actions.add(
                ContactAction(
                    label = "Email ${match.value}",
                    intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:${match.value}"))
                )
            )
        }

        TELEGRAM_REGEX.findAll(text).forEach { match ->
            val handle = match.groupValues[1].removePrefix("@")
            actions.add(
                ContactAction(
                    label = "Open Telegram @$handle",
                    intent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://t.me/$handle")
                    )
                )
            )
        }

        if (text.contains("t.me/", ignoreCase = true)) {
            val url = text.substringAfter("t.me/", "").substringBefore(" ").trim()
            if (url.isNotBlank()) {
                actions.add(
                    ContactAction(
                        label = "Open Telegram",
                        intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/$url"))
                    )
                )
            }
        }

        INSTAGRAM_REGEX.findAll(text).forEach { match ->
            val handle = match.groupValues[1].removePrefix("@")
            actions.add(
                ContactAction(
                    label = "Open Instagram @$handle",
                    intent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://instagram.com/$handle")
                    )
                )
            )
        }

        FACEBOOK_REGEX.findAll(text).forEach { match ->
            val value = match.groupValues[1]
            val uri = if (value.startsWith("http")) value else "https://facebook.com/$value"
            actions.add(
                ContactAction(
                    label = "Open Facebook",
                    intent = Intent(Intent.ACTION_VIEW, uri.toUri())
                )
            )
        }

        URL_REGEX.findAll(text).forEach { match ->
            val url = match.value
            if (!url.contains("instagram.com", true) &&
                !url.contains("facebook.com", true) &&
                !url.contains("t.me", true)
            ) {
                actions.add(
                    ContactAction(
                        label = "Open link",
                        intent = Intent(Intent.ACTION_VIEW, url.toUri())
                    )
                )
            }
        }

        if (actions.isEmpty() && text.length >= 3) {
            actions.add(
                ContactAction(
                    label = "Copy contact info",
                    intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, text)
                    }
                )
            )
        }

        return actions.distinctBy { it.label }
    }

    fun bindContactView(
        context: Context,
        textView: TextView,
        contactText: String,
        emptyText: String
    ) {
        val trimmed = contactText.trim()
        if (trimmed.isBlank()) {
            textView.text = emptyText
            textView.isClickable = false
            textView.setOnClickListener(null)
            return
        }

        textView.text = trimmed
        val actions = parseActions(trimmed)
        if (actions.isEmpty()) {
            textView.isClickable = false
            textView.setOnClickListener(null)
            return
        }

        textView.isClickable = true
        textView.paint.isUnderlineText = true
        textView.setOnClickListener {
            showActionChooser(context, trimmed, actions)
        }
    }

    private fun showActionChooser(
        context: Context,
        contactText: String,
        actions: List<ContactAction>
    ) {
        if (actions.size == 1) {
            launchAction(context, actions.first())
            return
        }

        val labels = actions.map { it.label }.toTypedArray()
        MaterialAlertDialogBuilder(context)
            .setTitle(contactText)
            .setItems(labels) { _, which ->
                launchAction(context, actions[which])
            }
            .show()
    }

    private fun launchAction(context: Context, action: ContactAction) {
        val intent = Intent.createChooser(action.intent, action.label)
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        }
    }

    private val PHONE_REGEX = Regex("""(\+?\d[\d\s\-().]{6,}\d)""")
    private val EMAIL_REGEX = Regex("""[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}""", RegexOption.IGNORE_CASE)
    private val TELEGRAM_REGEX = Regex("""(?:telegram|tg)\s*[:@]?\s*@?([A-Za-z0-9_]{3,})""", RegexOption.IGNORE_CASE)
    private val INSTAGRAM_REGEX = Regex("""(?:instagram|ig)\s*[:@]?\s*@?([A-Za-z0-9._]{2,})""", RegexOption.IGNORE_CASE)
    private val FACEBOOK_REGEX = Regex("""(?:facebook|fb)\s*[:.]?\s*([A-Za-z0-9.]+|https?://\S+)""", RegexOption.IGNORE_CASE)
    private val URL_REGEX = Regex("""https?://\S+""")
}
