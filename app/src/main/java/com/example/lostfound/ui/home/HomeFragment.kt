package com.example.lostfound.ui.home

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lostfound.R
import com.example.lostfound.databinding.FragmentHomeBinding
import com.example.lostfound.model.Item
import com.example.lostfound.ui.detail.ItemDetailActivity
import com.example.lostfound.util.ThemeToggleBinding
import com.google.android.material.chip.Chip

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by activityViewModels()
    private lateinit var adapter: ItemAdapter
    private lateinit var layoutManager: LinearLayoutManager
    private val searchHandler = Handler(Looper.getMainLooper())
    private var pendingSearchQuery: Runnable? = null

    companion object {
        private const val SEARCH_DEBOUNCE_MS = 300L
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupSwipeRefresh()
        setupFilters()
        setupFilterScroll()
        setupSearch()
        observeViewModel()
        ThemeToggleBinding.bind(binding.darkModeButton, requireActivity() as AppCompatActivity)

        binding.retryButton.setOnClickListener { viewModel.loadItems() }
        binding.addItemFab.setOnClickListener {
            findNavController().navigate(R.id.postItemFragment)
        }

        viewModel.loadItemsIfNeeded()
    }

    override fun onResume() {
        super.onResume()
        ThemeToggleBinding.refreshForFragment(this)
        viewModel.getScrollState()?.let { layoutManager.onRestoreInstanceState(it) }
    }

    override fun onPause() {
        viewModel.saveScrollState(layoutManager.onSaveInstanceState())
        super.onPause()
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setColorSchemeColors(
            ContextCompat.getColor(requireContext(), R.color.primary)
        )
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refreshItems()
        }
    }

    private fun setupRecyclerView() {
        adapter = ItemAdapter { item -> openItemDetail(item) }
        layoutManager = LinearLayoutManager(requireContext())
        binding.itemsRecyclerView.layoutManager = layoutManager
        binding.itemsRecyclerView.adapter = adapter
        binding.itemsRecyclerView.setHasFixedSize(true)
        binding.itemsRecyclerView.itemAnimator?.changeDuration = 0
    }

    private fun setupFilters() {
        val filters = resources.getStringArray(R.array.filters)
        val selected = viewModel.selectedFilter.value ?: "All"
        binding.filterChipGroup.setOnCheckedStateChangeListener(null)
        binding.filterChipGroup.removeAllViews()

        filters.forEach { filter ->
            val chip = Chip(requireContext()).apply {
                id = View.generateViewId()
                text = filter
                isCheckable = true
                isChecked = filter == selected
            }
            binding.filterChipGroup.addView(chip)
        }

        binding.filterChipGroup.setOnCheckedStateChangeListener { group, checkedIds ->
            if (checkedIds.isEmpty()) {
                syncChipSelection(viewModel.selectedFilter.value ?: "All")
                return@setOnCheckedStateChangeListener
            }
            val chip = group.findViewById<Chip>(checkedIds.first()) ?: return@setOnCheckedStateChangeListener
            val filter = chip.text.toString()
            if (filter != viewModel.selectedFilter.value) {
                viewModel.setSelectedFilter(filter)
                updateChipStyles(filter)
            }
        }

        updateChipStyles(selected)
    }

    private fun syncChipSelection(filter: String) {
        for (i in 0 until binding.filterChipGroup.childCount) {
            val chip = binding.filterChipGroup.getChildAt(i) as Chip
            if (chip.text.toString() == filter) {
                if (binding.filterChipGroup.checkedChipId != chip.id) {
                    binding.filterChipGroup.check(chip.id)
                }
                updateChipStyles(filter)
                return
            }
        }
    }

    private fun setupFilterScroll() {
        binding.filterScrollView.setOnTouchListener { view, event ->
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN ->
                    view.parent?.requestDisallowInterceptTouchEvent(true)
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL ->
                    view.parent?.requestDisallowInterceptTouchEvent(false)
            }
            false
        }
    }

    private fun updateChipStyles(selectedFilter: String) {
        for (i in 0 until binding.filterChipGroup.childCount) {
            val chip = binding.filterChipGroup.getChildAt(i) as Chip
            val selected = chip.text.toString() == selectedFilter
            chip.setChipBackgroundColorResource(
                if (selected) R.color.primary else R.color.surface_container_high
            )
            chip.setTextColor(
                resources.getColor(
                    if (selected) R.color.on_primary else R.color.on_surface_variant,
                    null
                )
            )
        }
    }

    private fun setupSearch() {
        binding.searchInput.setText(viewModel.searchQuery.value)
        viewModel.searchQuery.observe(viewLifecycleOwner) { query ->
            val current = binding.searchInput.text?.toString().orEmpty()
            if (current != query) {
                binding.searchInput.setText(query)
                binding.searchInput.setSelection(query.length)
            }
        }
        binding.searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
            override fun afterTextChanged(s: Editable?) {
                pendingSearchQuery?.let { searchHandler.removeCallbacks(it) }
                val query = s?.toString().orEmpty()
                pendingSearchQuery = Runnable { viewModel.setSearchQuery(query) }
                searchHandler.postDelayed(pendingSearchQuery!!, SEARCH_DEBOUNCE_MS)
            }
        })
    }

    private fun observeViewModel() {
        viewModel.filteredItems.observe(viewLifecycleOwner) { items ->
            adapter.submitList(items) {
                if (items.isNotEmpty() &&
                    (viewModel.getScrollState() == null || binding.swipeRefresh.isRefreshing)
                ) {
                    binding.itemsRecyclerView.scrollToPosition(0)
                }
            }
            binding.emptyStateLayout.visibility =
                if (items.isEmpty() && viewModel.isLoading.value != true &&
                    viewModel.isRefreshing.value != true
                ) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            val refreshing = viewModel.isRefreshing.value == true
            binding.loadingProgress.visibility =
                if (loading && !refreshing) View.VISIBLE else View.GONE
        }

        viewModel.isRefreshing.observe(viewLifecycleOwner) { refreshing ->
            binding.swipeRefresh.isRefreshing = refreshing
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            binding.errorStateLayout.visibility =
                if (!error.isNullOrBlank()) View.VISIBLE else View.GONE
            binding.errorMessageText.text = error
        }

        viewModel.selectedFilter.observe(viewLifecycleOwner) { filter ->
            syncChipSelection(filter ?: "All")
        }
    }

    private fun openItemDetail(item: Item) {
        val intent = Intent(requireContext(), ItemDetailActivity::class.java).apply {
            putExtra(ItemDetailActivity.EXTRA_ITEM_ID, item.id)
        }
        startActivity(intent)
    }

    override fun onDestroyView() {
        searchHandler.removeCallbacksAndMessages(null)
        pendingSearchQuery = null
        super.onDestroyView()
        _binding = null
    }
}
