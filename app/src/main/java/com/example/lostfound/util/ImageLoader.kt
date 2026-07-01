package com.example.lostfound.util

import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.lostfound.R
import java.io.File

object ImageLoader {

    /** 96dp list thumbnail at 2x density. */
    private const val LIST_THUMB_PX = 192

    fun load(imageView: ImageView, url: String?) {
        loadInternal(imageView, ImageUrls.primary(url), maxSizePx = null)
    }

    fun loadThumbnail(imageView: ImageView, url: String?) {
        loadInternal(imageView, ImageUrls.primary(url), maxSizePx = LIST_THUMB_PX)
    }

    private fun loadInternal(imageView: ImageView, url: String?, maxSizePx: Int?) {
        if (url.isNullOrBlank()) {
            imageView.setImageResource(R.drawable.placeholder_item)
            return
        }

        val model: Any = when {
            url.startsWith("data:image") -> url
            url.startsWith("content://") || url.startsWith("file://") -> Uri.parse(url)
            File(url).exists() -> File(url)
            else -> url
        }

        var request = Glide.with(imageView.context)
            .load(model)
            .transition(DrawableTransitionOptions.withCrossFade())
            .placeholder(R.drawable.placeholder_item)
            .error(R.drawable.placeholder_item)
            .centerCrop()

        if (maxSizePx != null) {
            request = request.override(maxSizePx, maxSizePx)
        }

        request.into(imageView)
    }
}
