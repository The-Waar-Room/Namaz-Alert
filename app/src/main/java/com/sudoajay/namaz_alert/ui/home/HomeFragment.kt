package com.sudoajay.namaz_alert.ui.home

import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.sudoajay.namaz_alert.R
import com.sudoajay.namaz_alert.databinding.FragmentHomeBinding
import com.sudoajay.namaz_alert.ui.BaseActivity.Companion.isSystemDefaultOn
import com.sudoajay.namaz_alert.ui.BaseFragment
import com.sudoajay.namaz_alert.ui.home.repository.DailyPrayerAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.*


@AndroidEntryPoint
class HomeFragment : BaseFragment() {
    private var isDarkTheme: Boolean = false

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val dailyPrayerViewModel: DailyPrayerViewModel by viewModels()

    lateinit var dailyPrayerAdapter: DailyPrayerAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        isDarkTheme = isSystemDefaultOn(resources)

        requireActivity().changeStatusBarColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.statusBarColor
            ), !isDarkTheme
        )

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        setupToolbar()
        setUpView()


        return binding.root
    }


    private fun setUpView() {

        binding.swipeRefresh.setColorSchemeResources(R.color.appTheme)
        binding.swipeRefresh.setProgressBackgroundColorSchemeColor(
            ContextCompat.getColor(requireContext(), R.color.statusBarColor)
        )


        //         Setup BottomAppBar Navigation Setup

        dailyPrayerAdapter = DailyPrayerAdapter {
            parentFragment?.findNavController()?.navigate(
                R.id.action_homeFragment_to_editDailyPrayerFragment,
                bundleOf(editDailyPrayerNameKey to it)
            )
        }


        callRecyclerDate()

//        lifecycleScope.launch {
//            dailyPrayerAbdulrcsViewModel.getDataFromApi()
//        }
    }

    private fun callRecyclerDate() {
        binding.recyclerView.apply {
            this.layoutManager = LinearLayoutManager(requireContext())
            adapter = dailyPrayerAdapter
        }
        lifecycleScope.launch {
            dailyPrayerViewModel.getRemoteMediatorWithDataBase()
                .collectLatest { pagingData ->

                    dailyPrayerAdapter.submitData(pagingData)
                }
        }

    }

    private fun setupToolbar() {
        setHasOptionsMenu(true)
        // The other option is using val toolbar = findViewById(R.id.toolvar)
        // and add as parameter instead of the binding option
        (activity as AppCompatActivity).setSupportActionBar(binding.bottomAppBar)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.bottom_toolbar_menu, menu)
        val actionSearch = menu.findItem(R.id.search_optionMenu)
        manageSearch(actionSearch)
    }


    private fun manageSearch(searchItem: MenuItem) {
        val searchView =
            searchItem.actionView as SearchView
        searchView.imeOptions = EditorInfo.IME_ACTION_SEARCH
        manageInputTextInSearchView(searchView)
    }

    private fun manageInputTextInSearchView(searchView: SearchView) {
        searchView.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                val query: String = newText.lowercase(Locale.ROOT).trim { it <= ' ' }
                dailyPrayerViewModel.searchValue = query
                refreshData()
                Log.e("SomethingNew", "onQueryTextChange  +  $query")
                return true
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.refresh_optionMenu -> {
                Log.e("SomethingNew", "refresh_optionMenu  +  ")
                true
            }
            R.id.sendFeedBack_optionMenu -> {
                Log.e("SomethingNew", "sendFeedBack_optionMenu  + ")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    private fun refreshData() {
        dailyPrayerAdapter.refresh()
    }


    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }
}
