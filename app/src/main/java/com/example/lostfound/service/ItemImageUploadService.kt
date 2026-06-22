package com.example.lostfound.service

import android.content.Context
import com.example.lostfound.BuildConfig
import com.example.lostfound.R
import com.example.lostfound.util.ImageStorageUtil
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * Uploads item photos to Firebase Storage when configured, otherwise falls back to base64 for MockAPI.
 */
@Singleton
class ItemImageUploadService @Inject constructor(
    @ApplicationContext private val appContext: Context
) {

    private val firebaseStorage by lazy { FirebaseStorageService(appContext) }

    suspend fun uploadForApi(localPathOrUri: String): String {
        if (localPathOrUri.isBlank()) return ""

        return suspendCoroutine { continuation ->
            uploadForApi(
                localPathOrUri = localPathOrUri,
                onSuccess = { url ->
                    if (url.isBlank()) {
                        continuation.resumeWithException(
                            Exception(appContext.getString(R.string.error_photo_upload_failed))
                        )
                    } else {
                        continuation.resume(url)
                    }
                },
                onError = { message ->
                    continuation.resumeWithException(
                        Exception(
                            message.ifBlank {
                                appContext.getString(R.string.error_photo_upload_failed)
                            }
                        )
                    )
                }
            )
        }
    }

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
            onError(appContext.getString(R.string.error_photo_upload_failed))
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
            onError(appContext.getString(R.string.error_photo_upload_failed))
        } else {
            onSuccess(encoded)
        }
    }
}
