package com.sudoajay.namaz_alert.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import androidx.activity.OnBackPressedCallback
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
import com.sudoajay.namaz_alert.ui.bottomSheet.NavigationDrawerBottomSheet
import com.sudoajay.namaz_alert.ui.home.repository.DailyPrayerAdapter
import com.sudoajay.namaz_alert.ui.setting.SettingsActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class HomeFragment : BaseFragment() {
    private var isDarkTheme: Boolean = false

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val dailyPrayerViewModel: DailyPrayerViewModel by viewModels()
    private var doubleBackToExitPressedOnce = false


    lateinit var dailyPrayerAdapter: DailyPrayerAdapter

    @Inject
    lateinit var navigationDrawerBottomSheet: NavigationDrawerBottomSheet

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
        binding.viewModel = dailyPrayerViewModel
        binding.lifecycleOwner = this
        setupToolbar()
        setUpView()




        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    onBack()
                }
            })
    }


    private fun setUpView() {
        binding.swipeRefresh.setColorSchemeResources(R.color.appTheme)
        binding.swipeRefresh.setProgressBackgroundColorSchemeColor(
            ContextCompat.getColor(requireContext(), R.color.statusBarColor)
        )

        binding.swipeRefresh.setOnRefreshListener {
            refreshData()
        }

        //         Setup BottomAppBar Navigation Setup

        dailyPrayerAdapter = DailyPrayerAdapter {
            parentFragment?.findNavController()?.navigate(
                R.id.action_homeFragment_to_editDailyPrayerFragment,
                bundleOf(
                    editDailyPrayerNameKey to it.Name,
                    editDailyPrayerTimeKey to it.Time
                )
            )
        }


        callRecyclerDate()


    }

    private fun callRecyclerDate() {
        binding.recyclerView.apply {
            this.layoutManager = LinearLayoutManager(requireContext())
            adapter = dailyPrayerAdapter
        }
        lifecycleScope.launch {
            dailyPrayerViewModel.getPagingGsonSourceWithNetwork().collectLatest {
                dailyPrayerViewModel.isLoadData.postValue(false)
                dailyPrayerAdapter.submitData(it)
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
        when (item.itemId) {
            android.R.id.home -> showNavigationDrawer()
            R.id.refresh_optionMenu -> {
                refreshData()
            }
            R.id.setting_optionMenu -> {
                openSetting()
            }
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun openSetting() {
        val intent = Intent(requireContext(), SettingsActivity::class.java)
        startActivity(intent)
    }


    private fun showNavigationDrawer() {
        navigationDrawerBottomSheet.show(
            childFragmentManager.beginTransaction(),
            navigationDrawerBottomSheet.tag
        )
    }

    private fun refreshData() {
        CoroutineScope(Dispatchers.Main).launch {
            binding.swipeRefresh.isRefreshing = true
            delay(1000 * 2) // 5 sec
            dailyPrayerAdapter.refresh()
            binding.swipeRefresh.isRefreshing = false
            dailyPrayerViewModel.isLoadData.value = true
        }
    }


    private fun onBack() {
        if (doubleBackToExitPressedOnce) {
            closeApp()
            return
        }
        doubleBackToExitPressedOnce = true
        throwToaster(getString(R.string.click_back_text))
        CoroutineScope(Dispatchers.IO).launch {
            delay(2000L)
            doubleBackToExitPressedOnce = false
        }
    }

    private fun closeApp() {
        val homeIntent = Intent(Intent.ACTION_MAIN)
        homeIntent.addCategory(Intent.CATEGORY_HOME)
        homeIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(homeIntent)
    }


    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }
}
