package com.example.lostfound.ui.detail

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lostfound.R
import com.example.lostfound.databinding.ActivityItemDetailBinding
import com.example.lostfound.util.ContactLinkHelper
import com.example.lostfound.util.DateUtils
import com.example.lostfound.util.ImageLoader
import com.example.lostfound.util.ImageUrls
import com.example.lostfound.util.LocationHelper
import com.example.lostfound.util.MapHelper
import com.example.lostfound.util.StatusUtils
import com.example.lostfound.util.SystemBars
import com.google.android.material.snackbar.Snackbar

import com.example.lostfound.service.SessionManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ItemDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityItemDetailBinding
    private val viewModel: ItemDetailViewModel by viewModels()
    private var detailPhotoAdapter: DetailPhotoAdapter? = null

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityItemDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        SystemBars.apply(activity = this, root = binding.root)

        val itemId = intent.getStringExtra(EXTRA_ITEM_ID)
        if (itemId.isNullOrBlank()) {
            finish()
            return
        }

        binding.backButton.setOnClickListener { finish() }
        observeViewModel()
        viewModel.loadItem(itemId)
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    binding.detailLoading.visibility = if (state.isLoading) View.VISIBLE else View.GONE
                    
                    if (state.error != null) {
                        Snackbar.make(binding.root, state.error, Snackbar.LENGTH_SHORT).show()
                    }

                    if (state.claimSuccess) {
                        Snackbar.make(binding.root, R.string.success_claimed, Snackbar.LENGTH_SHORT).show()
                        viewModel.clearClaimSuccess()
                    }

                    val item = state.item ?: return@collect
                    binding.detailTitle.text = item.title.orEmpty()
                    binding.detailDescription.text = item.description.orEmpty()

                    val location = LocationHelper.formatDisplayLocation(item.location)
                    binding.detailLocation.text = location.ifBlank { getString(R.string.location_not_set) }
                    setupLocationActions(location)

                    val reporter = item.reporterName?.ifBlank { null } ?: "Unknown"
                    binding.detailReporter.text = reporter

                    val contact = item.contactInfo.orEmpty().trim()
                    if (contact.isNotBlank()) {
                        ContactLinkHelper.bindContactView(
                            context = this@ItemDetailActivity,
                            textView = binding.detailContact,
                            contactText = contact,
                            emptyText = "",
                        )
                        binding.contactTapHint.visibility = View.VISIBLE
                    } else {
                        binding.detailContact.text = getString(R.string.contact_missing_hint, reporter)
                        binding.detailContact.paint.isUnderlineText = false
                        binding.detailContact.isClickable = false
                        binding.contactTapHint.visibility = View.GONE
                    }

                    binding.detailDate.text = DateUtils.formatDetailDate(item.createdAt ?: item.date)
                    binding.detailTime.text = DateUtils.formatDetailTime(item.createdAt ?: item.date)
                    StatusUtils.applyStatusBadge(this@ItemDetailActivity, item.status, binding.detailStatusBadge)
                    bindPhotos(item.imageUrl)

                    val isOwner = sessionManager.ownsItem(item.reporterName)
                    val canClaim = isOwner && !item.status.equals("claimed", ignoreCase = true)
                    binding.markClaimedButton.visibility = if (canClaim) View.VISIBLE else View.GONE
                    binding.markClaimedButton.setOnClickListener {
                        viewModel.updateItemStatus("claimed")
                    }
                }
            }
        }
    }

    private fun bindPhotos(imageValue: String?) {
        val photos = ImageUrls.split(imageValue)
        val primary = photos.firstOrNull()
        ImageLoader.load(binding.detailImage, primary)

        if (photos.size > 1) {
            binding.detailPhotoGallery.visibility = View.VISIBLE
            if (detailPhotoAdapter == null) {
                detailPhotoAdapter = DetailPhotoAdapter { url ->
                    ImageLoader.load(binding.detailImage, url)
                }
                binding.detailPhotoGallery.layoutManager = LinearLayoutManager(
                    this,
                    LinearLayoutManager.HORIZONTAL,
                    false
                )
                binding.detailPhotoGallery.adapter = detailPhotoAdapter
            }
            detailPhotoAdapter?.submitList(photos)
        } else {
            binding.detailPhotoGallery.visibility = View.GONE
            detailPhotoAdapter?.submitList(emptyList())
        }
    }

    private fun setupLocationActions(location: String) {
        if (location.isBlank()) {
            binding.openMapsButton.visibility = View.GONE
            binding.detailLocation.setOnClickListener(null)
            binding.detailLocation.isClickable = false
            return
        }

        binding.openMapsButton.visibility = View.VISIBLE
        val openMaps = {
            if (!MapHelper.openLocation(this, location)) {
                Snackbar.make(binding.root, R.string.maps_not_available, Snackbar.LENGTH_SHORT).show()
            }
        }
        binding.openMapsButton.setOnClickListener { openMaps() }
        binding.detailLocation.setOnClickListener { openMaps() }
        binding.detailLocation.isClickable = true
        binding.detailLocation.paint.isUnderlineText = true
    }

    companion object {
        const val EXTRA_ITEM_ID = "extra_item_id"
    }
}
