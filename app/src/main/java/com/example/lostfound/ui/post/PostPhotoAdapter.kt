package com.example.lostfound.ui.post

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.lostfound.databinding.ItemPostPhotoThumbBinding
import com.example.lostfound.util.ImageLoader

class PostPhotoAdapter(
    private val onRemove: (String) -> Unit
) : ListAdapter<String, PostPhotoAdapter.PhotoViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val binding = ItemPostPhotoThumbBinding.inflate(
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
        private val binding: ItemPostPhotoThumbBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(path: String) {
            ImageLoader.loadThumbnail(binding.photoThumb, path)
            binding.removePhotoButton.setOnClickListener { onRemove(path) }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean = oldItem == newItem
        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean = oldItem == newItem
    }
}
