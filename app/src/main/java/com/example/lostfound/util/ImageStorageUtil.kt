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

    /** MockAPI rejects large JSON bodies; keep JPEG small before base64 (~70KB → ~93KB encoded). */
    private const val MAX_IMAGE_BYTES = 70_000
    private const val MAX_EDGE_PX = 960

    fun persistImage(context: Context, uri: Uri): String? {
        return compressToLocalFile(context, uri)
    }

    fun persistBitmap(context: Context, bitmap: Bitmap): String? {
        return try {
            val bytes = compressBitmap(bitmap) ?: return null
            writeBytesToPostImageFile(context, bytes)
        } catch (_: Exception) {
            null
        } finally {
            if (!bitmap.isRecycled) bitmap.recycle()
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
            val bytes = readCompressedBytes(context, uri) ?: return ""
            "data:image/jpeg;base64," + Base64.encodeToString(bytes, Base64.NO_WRAP)
        } catch (_: Exception) {
            ""
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

    private fun compressToLocalFile(context: Context, uri: Uri): String? {
        return try {
            val bytes = readCompressedBytes(context, uri) ?: return null
            writeBytesToPostImageFile(context, bytes)
        } catch (_: Exception) {
            null
        }
    }

    private fun writeBytesToPostImageFile(context: Context, bytes: ByteArray): String? {
        return try {
            val dir = File(context.filesDir, "post_images").apply { mkdirs() }
            val outFile = File(dir, "${UUID.randomUUID()}.jpg")
            FileOutputStream(outFile).use { it.write(bytes) }
            outFile.absolutePath
        } catch (_: Exception) {
            null
        }
    }

    private fun compressBitmap(bitmap: Bitmap): ByteArray? {
        var quality = 80
        var scale = 1f
        var result = ByteArray(0)
        val base = scaleDownToMaxEdge(bitmap, MAX_EDGE_PX)

        for (attempt in 0 until 16) {
            val width = (base.width * scale).toInt().coerceAtLeast(1)
            val height = (base.height * scale).toInt().coerceAtLeast(1)
            val scaled = if (scale < 1f) {
                Bitmap.createScaledBitmap(base, width, height, true)
            } else {
                base
            }

            val stream = ByteArrayOutputStream()
            scaled.compress(Bitmap.CompressFormat.JPEG, quality, stream)
            result = stream.toByteArray()

            if (scaled != base) scaled.recycle()

            if (result.size <= MAX_IMAGE_BYTES) break
            if (quality > 30) {
                quality -= 10
            } else {
                scale *= 0.7f
            }
        }

        if (base != bitmap && !base.isRecycled) base.recycle()
        return result.takeIf { it.size <= MAX_IMAGE_BYTES }
    }

    private fun readCompressedBytes(context: Context, uri: Uri): ByteArray? {
        val original = context.contentResolver.openInputStream(uri)?.use { stream ->
            BitmapFactory.decodeStream(stream)
        } ?: File(uri.path.orEmpty()).takeIf { it.exists() }?.let {
            BitmapFactory.decodeFile(it.absolutePath)
        } ?: return null

        return try {
            compressBitmap(original)
        } finally {
            if (!original.isRecycled) original.recycle()
        }
    }

    private fun scaleDownToMaxEdge(bitmap: Bitmap, maxEdge: Int): Bitmap {
        val longest = maxOf(bitmap.width, bitmap.height)
        if (longest <= maxEdge) return bitmap
        val scale = maxEdge.toFloat() / longest
        val width = (bitmap.width * scale).toInt().coerceAtLeast(1)
        val height = (bitmap.height * scale).toInt().coerceAtLeast(1)
        return Bitmap.createScaledBitmap(bitmap, width, height, true)
    }
}
