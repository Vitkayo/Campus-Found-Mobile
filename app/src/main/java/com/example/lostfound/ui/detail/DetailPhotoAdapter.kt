package com.example.lostfound.ui.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.lostfound.databinding.ItemDetailPhotoThumbBinding
import com.example.lostfound.util.ImageLoader

class DetailPhotoAdapter(
    private val onPhotoClick: (String) -> Unit
) : ListAdapter<String, DetailPhotoAdapter.PhotoViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val binding = ItemDetailPhotoThumbBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PhotoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class PhotoViewHolder(
        private val binding: ItemDetailPhotoThumbBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(url: String) {
            ImageLoader.loadThumbnail(binding.photoThumb, url)
            binding.root.setOnClickListener { onPhotoClick(url) }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean = oldItem == newItem
        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean = oldItem == newItem
    }
}
