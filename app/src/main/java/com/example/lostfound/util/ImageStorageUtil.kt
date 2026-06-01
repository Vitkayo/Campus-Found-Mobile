package com.example.lostfound.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

object ImageStorageUtil {

    /** MockAPI rejects large JSON payloads; keep base64 JPEG small. */
    private const val MAX_IMAGE_BYTES = 180_000

    fun persistImage(context: Context, uri: Uri): String? {
        return try {
            val dir = File(context.filesDir, "post_images").apply { mkdirs() }
            val outFile = File(dir, "${UUID.randomUUID()}.jpg")
            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(outFile).use { output -> input.copyTo(output) }
            } ?: return null
            outFile.absolutePath
        } catch (_: Exception) {
            null
        }
    }

    fun toApiImageValue(context: Context, localPathOrUri: String): String {
        if (localPathOrUri.isBlank()) return ""
        if (localPathOrUri.startsWith("http://") || localPathOrUri.startsWith("https://")) {
            return localPathOrUri
        }
        if (localPathOrUri.startsWith("data:image")) {
            return localPathOrUri
        }

        val uri = when {
            localPathOrUri.startsWith("content://") || localPathOrUri.startsWith("file://") ->
                Uri.parse(localPathOrUri)
            else -> Uri.fromFile(File(localPathOrUri))
        }

        return try {
            val bytes = readCompressedBytes(context, uri) ?: return localPathOrUri
            "data:image/jpeg;base64," + Base64.encodeToString(bytes, Base64.NO_WRAP)
        } catch (_: Exception) {
            localPathOrUri
        }
    }

    fun pathFromDraftValue(value: String): String? {
        if (value.isBlank()) return null
        if (value.startsWith("content://") || value.startsWith("file://") || File(value).exists()) {
            return value
        }
        return null
    }

    fun isReadableLocalImage(context: Context, localPathOrUri: String): Boolean {
        if (localPathOrUri.isBlank()) return false
        if (localPathOrUri.startsWith("http://") || localPathOrUri.startsWith("https://")) return true
        if (localPathOrUri.startsWith("data:image")) return true
        if (localPathOrUri.startsWith("content://") || localPathOrUri.startsWith("file://")) {
            return try {
                context.contentResolver.openInputStream(Uri.parse(localPathOrUri))?.use { true } ?: false
            } catch (_: Exception) {
                false
            }
        }
        return File(localPathOrUri).exists()
    }

    private fun readCompressedBytes(context: Context, uri: Uri): ByteArray? {
        val original = context.contentResolver.openInputStream(uri)?.use { stream ->
            BitmapFactory.decodeStream(stream)
        } ?: File(uri.path.orEmpty()).takeIf { it.exists() }?.let {
            BitmapFactory.decodeFile(it.absolutePath)
        } ?: return null

        var quality = 82
        var scale = 1f
        var result = ByteArray(0)

        for (attempt in 0 until 12) {
            val width = (original.width * scale).toInt().coerceAtLeast(1)
            val height = (original.height * scale).toInt().coerceAtLeast(1)
            val scaled = if (scale < 1f) {
                Bitmap.createScaledBitmap(original, width, height, true)
            } else {
                original
            }

            val stream = ByteArrayOutputStream()
            scaled.compress(Bitmap.CompressFormat.JPEG, quality, stream)
            result = stream.toByteArray()

            if (scaled != original) scaled.recycle()

            if (result.size <= MAX_IMAGE_BYTES) break
            if (quality > 35) {
                quality -= 10
            } else {
                scale *= 0.75f
            }
        }

        if (!original.isRecycled) original.recycle()
        return result
    }
}
