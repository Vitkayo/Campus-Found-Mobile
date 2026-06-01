package com.example.lostfound.ui.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.lostfound.databinding.ItemProfileCardBinding
import com.example.lostfound.model.Item
import com.example.lostfound.util.DateUtils
import com.example.lostfound.util.ImageLoader
import com.example.lostfound.util.StatusUtils

class ProfileItemAdapter(
    private val onItemClick: (Item) -> Unit,
    private val onDeleteClick: (Item) -> Unit
) : ListAdapter<Item, ProfileItemAdapter.ProfileViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileViewHolder {
        val binding = ItemProfileCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProfileViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProfileViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ProfileViewHolder(
        private val binding: ItemProfileCardBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Item) {
            binding.itemTitle.text = item.title.orEmpty()
            binding.itemDate.text = "Reported: ${DateUtils.formatPostedDate(item.createdAt ?: item.date)}"
            binding.itemLocation.text = item.location.orEmpty()
            StatusUtils.applyStatusBadge(binding.root.context, item.status, binding.statusBadge)
            ImageLoader.loadThumbnail(binding.itemImage, item.imageUrl)
            binding.root.setOnClickListener { onItemClick(item) }
            binding.deleteButton.setOnClickListener { onDeleteClick(item) }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<Item>() {
        override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean = oldItem == newItem
    }
}
