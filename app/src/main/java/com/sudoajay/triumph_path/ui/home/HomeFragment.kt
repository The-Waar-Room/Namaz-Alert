package com.sudoajay.triumph_path.ui.home

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.withCreated
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.sudoajay.triumph_path.R
import com.sudoajay.triumph_path.databinding.FragmentHomeBinding
import com.sudoajay.triumph_path.ui.BaseActivity.Companion.isSystemDefaultOn
import com.sudoajay.triumph_path.ui.BaseFragment
import com.sudoajay.triumph_path.ui.bottomSheet.NavigationDrawerBottomSheet
import com.sudoajay.triumph_path.ui.home.repository.DailyPrayerAdapter
import com.sudoajay.triumph_path.ui.setting.SettingsActivity
import com.sudoajay.triumph_path.util.Helper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@AndroidEntryPoint
class HomeFragment : BaseFragment() {
    private var isDarkTheme: Boolean = false

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding

    private val dailyPrayerViewModel: DailyPrayerViewModel by viewModels()
    private var doubleBackToExitPressedOnce = false


//    private lateinit var dailyPrayerAdapter: DailyPrayerAdapter

    @Inject
    lateinit var navigationDrawerBottomSheet: NavigationDrawerBottomSheet

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        isDarkTheme = isSystemDefaultOn(resources)

        requireActivity().changeStatusBarColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.statusBarColor
            ), !isDarkTheme
        )


        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding?.viewModel = dailyPrayerViewModel
        binding?.lifecycleOwner = this
        setupToolbar()
        setUpView()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !Helper.isNotificationPermissionAsked(
                requireContext()
            )
        ) {
            openNotifyMe()
        }


        return binding!!.root
    }

    private fun openNotifyMe() {
        findNavController().navigate(R.id.action_homeFragment_to_navigation_firebasePushNotificationFragment)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    onBack()
                }
            })

        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                // Add menu items here
                menuInflater.inflate(R.menu.bottom_toolbar_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                // Handle the menu selection
                return when (menuItem.itemId) {
                    android.R.id.home -> {
                        showNavigationDrawer()
                        true
                    }

                    R.id.refresh_optionMenu -> {
                        refreshData()
                        true
                    }

                    R.id.setting_optionMenu -> {
                        openSetting()
                        true
                    }

                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)


    }


    private fun setUpView() {
        binding?.swipeRefresh?.setColorSchemeResources(R.color.appTheme)
        binding?.swipeRefresh?.setProgressBackgroundColorSchemeColor(
            ContextCompat.getColor(requireContext(), R.color.statusBarColor)
        )

        binding?.swipeRefresh?.setOnRefreshListener {
            refreshData()
        }

        //         Setup BottomAppBar Navigation Setup

//        dailyPrayerAdapter = DailyPrayerAdapter {
//            parentFragment?.findNavController()?.navigate(
//                R.id.action_homeFragment_to_editDailyPrayerFragment,
//                bundleOf(
//                    editDailyPrayerNameKey to it.Name,
//                    editDailyPrayerTimeKey to it.Time
//                )
//            )
//        }


        callRecyclerDate()


    }

    private fun callRecyclerDate() {
//        binding.recyclerView.apply {
//            this.layoutManager = LinearLayoutManager(requireContext())
//            adapter = dailyPrayerAdapter
//        }
        lifecycleScope.launch(Dispatchers.IO) {
//            dailyPrayerViewModel.getPagingGsonSourceWithDataBase().collectLatest {
//                dailyPrayerViewModel.isLoadData.postValue(false)
////                dailyPrayerAdapter.submitData(it)
//
//            }

            dailyPrayerViewModel.getAllDataFromDataBase().collectLatest {

                when (it.Name) {
                    fajrName -> {
                        binding?.include?.itemTextView1?.text = getString(
                            R.string.fajr_time_text,
                            Helper.convertTo12Hr(requireContext(), it.Time)
                        )

                        binding?.include?.cardView1?.setOnClickListener { view ->
                            parentFragment?.findNavController()?.navigate(
                                R.id.action_homeFragment_to_editDailyPrayerFragment,
                                bundleOf(
                                    editDailyPrayerNameKey to it.Name,
                                    editDailyPrayerTimeKey to it.Time
                                )
                            )
                        }


                    }

                    dhuhrName -> {
                        binding?.include?.itemTextView2?.text = getString(
                            R.string.dhuhr_time_text,

                            Helper.convertTo12Hr(requireContext(), it.Time)
                        )

                        binding?.include?.cardView2?.setOnClickListener { view ->
                            parentFragment?.findNavController()?.navigate(
                                R.id.action_homeFragment_to_editDailyPrayerFragment,
                                bundleOf(
                                    editDailyPrayerNameKey to it.Name,
                                    editDailyPrayerTimeKey to it.Time
                                )
                            )
                        }
                    }
                    asrName -> {
                        binding?.include?.itemTextView3?.text = getString(
                            R.string.asr_time_text,

                            Helper.convertTo12Hr(requireContext(), it.Time)
                        )

                        binding?.include?.cardView3?.setOnClickListener { view ->
                            parentFragment?.findNavController()?.navigate(
                                R.id.action_homeFragment_to_editDailyPrayerFragment,
                                bundleOf(
                                    editDailyPrayerNameKey to it.Name,
                                    editDailyPrayerTimeKey to it.Time
                                )
                            )
                        }
                    }
                    maghribName -> {
                        binding?.include?.itemTextView4?.text = getString(
                            R.string.maghrib_time_text,
                            Helper.convertTo12Hr(requireContext(), it.Time)
                        )

                        binding?.include?.cardView4?.setOnClickListener { view ->
                            parentFragment?.findNavController()?.navigate(
                                R.id.action_homeFragment_to_editDailyPrayerFragment,
                                bundleOf(
                                    editDailyPrayerNameKey to it.Name,
                                    editDailyPrayerTimeKey to it.Time
                                )
                            )
                        }
                    }
                    ishaName -> {
                        binding?.include?.itemTextView5?.text = getString(
                            R.string.isha_time_text,
                            Helper.convertTo12Hr(requireContext(), it.Time)
                        )

                        binding?.include?.cardView5?.setOnClickListener { view ->
                            parentFragment?.findNavController()?.navigate(
                                R.id.action_homeFragment_to_editDailyPrayerFragment,
                                bundleOf(
                                    editDailyPrayerNameKey to it.Name,
                                    editDailyPrayerTimeKey to it.Time
                                )
                            )
                        }
                    }
                }
                dailyPrayerViewModel.isLoadData.postValue(false)
            }




            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // this block is automatically executed when moving into
                // the started state, and cancelled when stopping.
                while (dailyPrayerViewModel.isLoadData.value == true) {
                    // the function to repeat
                    withContext(Dispatchers.Main) {
                        throwToaster(getString(R.string.open_internet_connection_text))
                    }

                    delay(1000 * 60 * 2) // 2 min

                }
            }

        }


    }

    private fun setupToolbar() {
        // The other option is using val toolbar = findViewById(R.id.toolvar)
        // and add as parameter instead of the binding option
        (activity as AppCompatActivity).setSupportActionBar(binding?.bottomAppBar)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
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
            binding?.swipeRefresh?.isRefreshing = true
            delay(1000 * 2) // 2 sec
//            dailyPrayerAdapter.refresh()
            binding?.swipeRefresh?.isRefreshing = false
            dailyPrayerViewModel.isLoadData.value = true
            callRecyclerDate()
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
