package com.example.lostfound.ui.post

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.doOnLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lostfound.R
import com.example.lostfound.databinding.FragmentPostItemBinding
import com.example.lostfound.service.SessionManager
import com.example.lostfound.ui.home.HomeViewModel
import com.example.lostfound.ui.map.MapPickerActivity
import com.example.lostfound.util.ImageStorageUtil
import com.example.lostfound.util.ImageUrls
import com.example.lostfound.util.LocationHelper
import com.example.lostfound.util.ThemeToggleBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.util.Calendar
import java.util.Locale
import java.util.UUID
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PostItemFragment : Fragment() {

    private var _binding: FragmentPostItemBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PostItemViewModel by viewModels()
    private val homeViewModel: HomeViewModel by activityViewModels()
    private lateinit var sessionManager: SessionManager
    private lateinit var photoAdapter: PostPhotoAdapter
    private val savedImagePaths = mutableListOf<String>()
    private var isLostSelected = true
    private var cameraPhotoUri: Uri? = null
    private var lastShownSubmitError: String? = null

    private val imagePicker = registerForActivityResult(
        ActivityResultContracts.GetMultipleContents(),
    ) { uris ->
        if (uris.isNotEmpty()) {
            addImagesFromUris(uris)
        }
    }

    private val mapPickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val location = result.data?.getStringExtra(MapPickerActivity.EXTRA_SELECTED_LOCATION)
            if (!location.isNullOrBlank()) {
                binding.locationInput.setText(location)
                saveDraftFromForm()
            }
        }
    }

    private val takePhotoLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture(),
    ) { success ->
        val uri = cameraPhotoUri
        if (success && uri != null) {
            onImageUriSelected(uri)
        }
        cameraPhotoUri = null
    }

    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        if (granted) {
            launchCameraCapture()
        } else {
            Snackbar.make(binding.root, R.string.camera_permission_needed, Snackbar.LENGTH_LONG).show()
        }
    }

    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        if (granted) {
            requestCurrentLocation()
        } else {
            Snackbar.make(binding.root, R.string.location_permission_needed, Snackbar.LENGTH_LONG).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionManager = SessionManager(requireContext())
        setupPhotoList()
        setupCategoryDropdown()
        setupStatusSelector()
        restoreDraft()
        setupListeners()
        observeViewModel()
        ThemeToggleBinding.bind(binding.darkModeButton, requireActivity() as AppCompatActivity)
    }

    private fun setupPhotoList() {
        photoAdapter = PostPhotoAdapter { path -> removePhoto(path) }
        binding.photoList.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.HORIZONTAL,
            false
        )
        binding.photoList.adapter = photoAdapter
    }

    private fun setupCategoryDropdown() {
        val categories = resources.getStringArray(R.array.categories)
        val adapter = android.widget.ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            categories
        )
        binding.categoryInput.setAdapter(adapter)
    }

    private fun setupStatusSelector() {
        binding.statusSelector.doOnLayout {
            updateStatusSelection(isLostSelected, animate = false)
        }

        binding.lostOption.setOnClickListener {
            updateStatusSelection(isLost = true, animate = true)
            saveDraftFromForm()
        }

        binding.foundOption.setOnClickListener {
            updateStatusSelection(isLost = false, animate = true)
            saveDraftFromForm()
        }
    }

    private fun updateStatusSelection(isLost: Boolean, animate: Boolean) {
        isLostSelected = isLost
        binding.lostOption.isSelected = isLost
        binding.foundOption.isSelected = !isLost

        val label = if (isLost) getString(R.string.lost) else getString(R.string.found)
        binding.selectedStatusLabel.text = getString(R.string.selected_status, label)

        val selectorWidth = binding.statusSelector.width
        if (selectorWidth == 0) {
            binding.statusSelector.viewTreeObserver.addOnGlobalLayoutListener(
                object : ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        binding.statusSelector.viewTreeObserver.removeOnGlobalLayoutListener(this)
                        moveStatusIndicator(isLost, animate)
                    }
                }
            )
        } else {
            moveStatusIndicator(isLost, animate)
        }
    }

    private fun moveStatusIndicator(isLost: Boolean, animate: Boolean) {
        val selectorWidth = binding.statusSelector.width
        val padding = binding.statusSelector.paddingLeft + binding.statusSelector.paddingRight
        val indicatorWidth = (selectorWidth - padding) / 2f

        val params = binding.statusIndicator.layoutParams
        params.width = indicatorWidth.toInt()
        binding.statusIndicator.layoutParams = params

        val targetX = if (isLost) {
            binding.statusSelector.paddingLeft.toFloat()
        } else {
            binding.statusSelector.paddingLeft + indicatorWidth
        }

        if (animate) {
            binding.statusIndicator.animate().x(targetX).setDuration(200).start()
        } else {
            binding.statusIndicator.x = targetX
        }
    }

    private fun restoreDraft() {
        val state = viewModel.uiState.value
        binding.titleInput.setText(state.title)
        binding.categoryInput.setText(state.category, false)
        binding.descriptionInput.setText(state.description)
        binding.locationInput.setText(state.location)
        val draftContact = state.contact
        binding.contactInput.setText(
            draftContact.ifBlank { sessionManager.getDefaultContact() }
        )
        binding.dateInput.setText(state.date)

        isLostSelected = state.status != "found"
        updateStatusSelection(isLostSelected, animate = false)

        savedImagePaths.clear()
        savedImagePaths.addAll(ImageStorageUtil.pathsFromDraftValue(state.imageUrl))
        refreshPhotoUi()
    }

    private fun refreshPhotoUi() {
        if (savedImagePaths.isEmpty()) {
            binding.photoList.visibility = View.GONE
            binding.uploadArea.visibility = View.VISIBLE
            binding.previewImage.visibility = View.GONE
            binding.uploadPlaceholder.visibility = View.VISIBLE
        } else {
            binding.photoList.visibility = View.VISIBLE
            binding.uploadArea.visibility = View.GONE
            photoAdapter.submitList(savedImagePaths.toList())
        }
    }

    private fun removePhoto(path: String) {
        savedImagePaths.remove(path)
        refreshPhotoUi()
        saveDraftFromForm()
    }

    private fun launchImagePicker() {
        if (savedImagePaths.size >= ImageUrls.MAX_PHOTOS) {
            Snackbar.make(
                binding.root,
                getString(R.string.photo_limit_reached, ImageUrls.MAX_PHOTOS),
                Snackbar.LENGTH_SHORT
            ).show()
            return
        }
        imagePicker.launch("image/*")
    }

    private fun addImagesFromUris(uris: List<Uri>) {
        val remaining = ImageUrls.MAX_PHOTOS - savedImagePaths.size
        if (remaining <= 0) {
            Snackbar.make(
                binding.root,
                getString(R.string.photo_limit_reached, ImageUrls.MAX_PHOTOS),
                Snackbar.LENGTH_SHORT
            ).show()
            return
        }

        var added = 0
        for (uri in uris.take(remaining)) {
            val path = ImageStorageUtil.persistImage(requireContext(), uri) ?: continue
            if (path in savedImagePaths) continue
            savedImagePaths.add(path)
            added++
        }

        if (added == 0) {
            Snackbar.make(binding.root, R.string.error_loading, Snackbar.LENGTH_SHORT).show()
            return
        }

        if (uris.size > remaining) {
            Snackbar.make(
                binding.root,
                getString(R.string.photo_limit_reached, ImageUrls.MAX_PHOTOS),
                Snackbar.LENGTH_SHORT
            ).show()
        }

        refreshPhotoUi()
        saveDraftFromForm()
    }

    private fun setupListeners() {
        binding.uploadArea.setOnClickListener { launchImagePicker() }
        binding.choosePhotoButton.setOnClickListener { launchImagePicker() }
        binding.takePhotoButton.setOnClickListener { onTakePhotoClicked() }
        binding.dateInput.setOnClickListener { showDatePicker() }
        binding.useLocationButton.setOnClickListener { onUseLocationClicked() }
        binding.pickMapButton.setOnClickListener { launchMapPicker() }

        binding.submitButton.setOnClickListener {
            val status = if (isLostSelected) "lost" else "found"
            val imagePaths = savedImagePaths.filter { path ->
                ImageStorageUtil.isReadableLocalImage(requireContext(), path)
            }
            viewModel.submitItem(
                titleValue = binding.titleInput.text?.toString().orEmpty(),
                categoryValue = binding.categoryInput.text?.toString().orEmpty(),
                descriptionValue = binding.descriptionInput.text?.toString().orEmpty(),
                locationValue = binding.locationInput.text?.toString().orEmpty(),
                contactValue = binding.contactInput.text?.toString().orEmpty(),
                dateValue = binding.dateInput.text?.toString().orEmpty(),
                statusValue = status,
                imagePathsOrJoined = ImageUrls.join(imagePaths)
            )
        }
    }

    private fun launchMapPicker() {
        val intent = Intent(requireContext(), MapPickerActivity::class.java).apply {
            putExtra(
                MapPickerActivity.EXTRA_INITIAL_LOCATION,
                binding.locationInput.text?.toString().orEmpty()
            )
        }
        mapPickerLauncher.launch(intent)
    }

    private fun onImageUriSelected(uri: Uri) {
        addImagesFromUris(listOf(uri))
    }

    private fun onTakePhotoClicked() {
        if (savedImagePaths.size >= ImageUrls.MAX_PHOTOS) {
            Snackbar.make(
                binding.root,
                getString(R.string.photo_limit_reached, ImageUrls.MAX_PHOTOS),
                Snackbar.LENGTH_SHORT
            ).show()
            return
        }

        val granted = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        if (granted) {
            launchCameraCapture()
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun launchCameraCapture() {
        val uri = createCameraPhotoUri() ?: run {
            Snackbar.make(binding.root, R.string.error_loading, Snackbar.LENGTH_SHORT).show()
            return
        }
        cameraPhotoUri = uri
        takePhotoLauncher.launch(uri)
    }

    private fun createCameraPhotoUri(): Uri? {
        return try {
            val file = File(requireContext().cacheDir, "camera_${UUID.randomUUID()}.jpg")
            FileProvider.getUriForFile(
                requireContext(),
                "${requireContext().packageName}.fileprovider",
                file
            )
        } catch (_: Exception) {
            null
        }
    }

    private fun onUseLocationClicked() {
        val granted = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (granted) {
            requestCurrentLocation()
        } else {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun requestCurrentLocation() {
        binding.useLocationButton.isEnabled = false
        LocationHelper.fetchCurrentLocation(
            context = requireContext(),
            onSuccess = { address ->
                binding.locationInput.setText(address)
                binding.useLocationButton.isEnabled = true
                saveDraftFromForm()
            },
        ) { message ->
            binding.useLocationButton.isEnabled = true
            Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                val formatted = String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, day)
                binding.dateInput.setText(formatted)
                saveDraftFromForm()
            },
            calendar[Calendar.YEAR],
            calendar[Calendar.MONTH],
            calendar[Calendar.DAY_OF_MONTH]
        ).show()
    }

    private fun saveDraftFromForm() {
        val status = if (isLostSelected) "lost" else "found"
        viewModel.saveDraft(
            titleValue = binding.titleInput.text?.toString().orEmpty(),
            categoryValue = binding.categoryInput.text?.toString().orEmpty(),
            descriptionValue = binding.descriptionInput.text?.toString().orEmpty(),
            locationValue = binding.locationInput.text?.toString().orEmpty(),
            contactValue = binding.contactInput.text?.toString().orEmpty(),
            dateValue = binding.dateInput.text?.toString().orEmpty(),
            statusValue = status,
            imageUrlValue = ImageUrls.join(savedImagePaths)
        )
    }

    override fun onResume() {
        super.onResume()
        ThemeToggleBinding.refreshForFragment(this)
    }

    override fun onPause() {
        saveDraftFromForm()
        super.onPause()
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collectLatest { state ->
                    if (state.error != null) {
                        binding.formErrorText.visibility = View.VISIBLE
                        binding.formErrorText.text = state.error
                        if (state.error != lastShownSubmitError) {
                            lastShownSubmitError = state.error
                            Snackbar.make(binding.root, state.error, Snackbar.LENGTH_LONG).show()
                        }
                    } else {
                        binding.formErrorText.visibility = View.GONE
                        lastShownSubmitError = null
                    }

                    binding.submitProgress.visibility = if (state.isSubmitting) View.VISIBLE else View.GONE
                    binding.submitButton.isEnabled = !state.isSubmitting

                    if (state.submitSuccess == true) {
                        Snackbar.make(binding.root, R.string.success_post, Snackbar.LENGTH_SHORT).show()
                        viewModel.clearDraft()
                        clearFormUi()
                        homeViewModel.refreshAfterNewPost()
                        findNavController().navigate(R.id.homeFragment)
                        viewModel.clearSubmitSuccess()
                    }
                }
            }
        }
    }

    private fun clearFormUi() {
        binding.titleInput.text = null
        binding.categoryInput.text = null
        binding.descriptionInput.text = null
        binding.locationInput.text = null
        binding.contactInput.text = null
        binding.dateInput.text = null
        binding.previewImage.visibility = View.GONE
        binding.uploadPlaceholder.visibility = View.VISIBLE
        binding.photoList.visibility = View.GONE
        binding.uploadArea.visibility = View.VISIBLE
        savedImagePaths.clear()
        photoAdapter.submitList(emptyList())
        isLostSelected = true
        updateStatusSelection(isLost = true, animate = false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
