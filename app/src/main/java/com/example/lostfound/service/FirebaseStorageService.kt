package com.example.lostfound.service

import android.content.Context
import android.net.Uri
import com.google.firebase.FirebaseApp
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.util.UUID

class FirebaseStorageService(private val context: Context) {

    private fun storageOrNull(): FirebaseStorage? {
        if (!isFirebaseReady(context)) return null
        return FirebaseStorage.getInstance()
    }

    fun uploadItemImage(
        localPath: String,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        val storage = storageOrNull()
        if (storage == null) {
            onError("Firebase is not configured. Add app/google-services.json (see google-services.json.example).")
            return
        }

        val file = File(localPath)
        if (!file.exists()) {
            onError("Image file not found")
            return
        }

        val objectName = "items/${UUID.randomUUID()}.jpg"
        val reference = storage.reference.child(objectName)

        reference.putFile(Uri.fromFile(file))
            .continueWithTask { task ->
                if (!task.isSuccessful) {
                    throw task.exception ?: Exception("Upload failed")
                }
                reference.downloadUrl
            }
            .addOnSuccessListener { uri ->
                onSuccess(uri.toString())
            }
            .addOnFailureListener { error ->
                onError(error.message ?: "Image upload failed")
            }
    }

    companion object {
        fun isFirebaseReady(context: Context): Boolean {
            return try {
                FirebaseApp.getApps(context).isNotEmpty()
            } catch (_: Exception) {
                false
            }
        }
    }
}
