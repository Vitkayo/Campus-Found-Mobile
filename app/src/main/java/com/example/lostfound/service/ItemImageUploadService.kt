package com.example.lostfound.service

import android.content.Context
import com.example.lostfound.BuildConfig
import com.example.lostfound.util.ImageStorageUtil

/**
 * Uploads item photos to Firebase Storage when configured, otherwise falls back to base64 for MockAPI.
 */
class ItemImageUploadService(context: Context) {

    private val appContext = context.applicationContext
    private val firebaseStorage by lazy { FirebaseStorageService(appContext) }

    fun uploadForApi(
        localPathOrUri: String,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        if (localPathOrUri.isBlank()) {
            onSuccess("")
            return
        }

        if (localPathOrUri.startsWith("http://") || localPathOrUri.startsWith("https://")) {
            onSuccess(localPathOrUri)
            return
        }

        if (!ImageStorageUtil.isReadableLocalImage(appContext, localPathOrUri)) {
            onSuccess("")
            return
        }

        if (BuildConfig.FIREBASE_ENABLED && FirebaseStorageService.isFirebaseReady(appContext)) {
            firebaseStorage.uploadItemImage(
                localPath = localPathOrUri,
                onSuccess = onSuccess,
                onError = { uploadWithBase64Fallback(localPathOrUri, onSuccess, onError) }
            )
            return
        }

        uploadWithBase64Fallback(localPathOrUri, onSuccess, onError)
    }

    private fun uploadWithBase64Fallback(
        localPathOrUri: String,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        val encoded = ImageStorageUtil.toApiImageValue(appContext, localPathOrUri)
        if (encoded.isBlank()) {
            // Photo is optional — continue posting without blocking on image issues.
            onSuccess("")
        } else {
            onSuccess(encoded)
        }
    }
}
