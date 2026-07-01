package com.example.lostfound.ui.home

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lostfound.R
import com.example.lostfound.databinding.FragmentHomeBinding
import com.example.lostfound.model.Item
import com.example.lostfound.ui.detail.ItemDetailActivity
import com.example.lostfound.util.ThemeToggleBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by activityViewModels()
    private lateinit var adapter: ItemAdapter
    private lateinit var layoutManager: LinearLayoutManager
    private val searchHandler = Handler(Looper.getMainLooper())
    private var pendingSearchQuery: Runnable? = null
    private var categoryDropdownOptions: List<String> = emptyList()
    private lateinit var statusFilterButtons: List<TextView>

    companion object {
        private const val SEARCH_DEBOUNCE_MS = 300L
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.root.requestFocus()
        setupRecyclerView()
        setupSwipeRefresh()
        setupStatusFilters()
        setupCategoryDropdown()
        setupSearch()
        observeViewModel()
        ThemeToggleBinding.bind(binding.darkModeButton, requireActivity() as AppCompatActivity)

        binding.retryButton.setOnClickListener { viewModel.loadItems() }
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
            ContextCompat.getColor(requireContext(), R.color.primary),
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

    private fun setupStatusFilters() {
        statusFilterButtons = listOf(
            binding.filterAllButton,
            binding.filterLostButton,
            binding.filterFoundButton,
        )

        statusFilterButtons.forEach { button ->
            button.setOnClickListener {
                val filter = button.text.toString()
                if (filter != viewModel.uiState.value.selectedStatusFilter) {
                    viewModel.setSelectedStatusFilter(filter)
                }
            }
        }

        updateStatusFilterStyles(viewModel.uiState.value.selectedStatusFilter)
    }

    private fun setupCategoryDropdown() {
        categoryDropdownOptions = buildList {
            add(getString(R.string.home_category_filter_all))
            addAll(resources.getStringArray(R.array.categories))
        }

        val dropdownAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            categoryDropdownOptions
        )
        binding.categoryFilterInput.setAdapter(dropdownAdapter)
        binding.categoryFilterInput.setOnClickListener {
            binding.categoryFilterInput.showDropDown()
        }
        binding.categoryFilterInput.setOnItemClickListener { _, _, position, _ ->
            val category = if (position == 0) "" else categoryDropdownOptions[position]
            if (category != viewModel.uiState.value.selectedCategory) {
                viewModel.setSelectedCategory(category)
            }
        }

        syncCategoryDropdown(viewModel.uiState.value.selectedCategory)
    }

    private fun syncStatusFilterSelection(filter: String) {
        updateStatusFilterStyles(filter)
    }

    private fun syncCategoryDropdown(category: String) {
        val display = if (category.isBlank()) {
            getString(R.string.home_category_filter_all)
        } else {
            category
        }
        if (binding.categoryFilterInput.text?.toString() != display) {
            binding.categoryFilterInput.setText(display, false)
        }
    }

    private fun updateStatusFilterStyles(selectedFilter: String) {
        statusFilterButtons.forEach { button ->
            val selected = button.text.toString() == selectedFilter
            button.isSelected = selected
        }
    }

    private fun setupSearch() {
        binding.searchInput.setText(viewModel.uiState.value.searchQuery)
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
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collectLatest { state ->
                adapter.submitList(state.items) {
                    if (state.items.isNotEmpty() && (viewModel.getScrollState() == null || state.isRefreshing)) {
                        binding.itemsRecyclerView.scrollToPosition(0)
                    }
                }

                binding.emptyStateLayout.visibility =
                    if (state.items.isEmpty() && !state.isLoading && !state.isRefreshing) View.VISIBLE else View.GONE

                binding.loadingProgress.visibility =
                    if (state.isLoading && !state.isRefreshing) View.VISIBLE else View.GONE

                binding.swipeRefresh.isRefreshing = state.isRefreshing

                binding.errorStateLayout.visibility =
                    if (!state.error.isNullOrBlank()) View.VISIBLE else View.GONE
                binding.errorMessageText.text = state.error

                updateHomeCounts(state)
                syncStatusFilterSelection(state.selectedStatusFilter)
                syncCategoryDropdown(state.selectedCategory)

                val currentSearch = binding.searchInput.text?.toString().orEmpty()
                if (currentSearch != state.searchQuery) {
                    binding.searchInput.setText(state.searchQuery)
                    binding.searchInput.setSelection(state.searchQuery.length)
                }
            }
        }
    }

    private fun openItemDetail(item: Item) {
        val intent = Intent(requireContext(), ItemDetailActivity::class.java).apply {
            putExtra(ItemDetailActivity.EXTRA_ITEM_ID, item.id)
        }
        startActivity(intent)
    }

    private fun updateHomeCounts(state: HomeUiState) {
        val lost = state.items.count { it.status.equals("lost", ignoreCase = true) }
        val found = state.items.count { it.status.equals("found", ignoreCase = true) }
        binding.lostCountText.text = getString(R.string.home_lost_count, lost)
        binding.foundCountText.text = getString(R.string.home_found_count, found)
        binding.totalCountText.text = getString(R.string.home_total_count, state.items.size)
        binding.resultsCountText.text = getString(R.string.home_results_count, state.items.size)
    }

    override fun onDestroyView() {
        searchHandler.removeCallbacksAndMessages(null)
        pendingSearchQuery = null
        super.onDestroyView()
        _binding = null
    }
}
