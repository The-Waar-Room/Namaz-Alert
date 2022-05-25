package com.sudoajay.namaz_alert.ui.notificationSound

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager

import com.sudoajay.namaz_alert.R
import com.sudoajay.namaz_alert.databinding.FragmentSelectNotificationSoundBinding
import com.sudoajay.namaz_alert.ui.BaseActivity
import com.sudoajay.namaz_alert.ui.BaseFragment
import com.sudoajay.namaz_alert.ui.notificationSound.repository.SelectNotificationSoundAdapter
import com.sudoajay.namaz_alert.ui.setting.SettingsActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SelectNotificationSoundFragment: BaseFragment() {

    private var _binding: FragmentSelectNotificationSoundBinding? = null
    private val binding get() = _binding!!

    private val selectLanguageViewModel: SelectNotificationSoundViewModel by viewModels()
    @Inject
    lateinit var selectNotificationSoundAdapter: SelectNotificationSoundAdapter
    private var isDarkTheme: Boolean = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        isDarkTheme = BaseActivity.isSystemDefaultOn(resources)
        requireActivity().changeStatusBarColor(
            ContextCompat.getColor(
                requireContext(),
                if(isDarkTheme)
                R.color.bgBoxColor else R.color.appTheme
            ), false
        )
        // Inflate the layout for this fragment
        _binding = FragmentSelectNotificationSoundBinding.inflate(inflater, container, false)
        binding.selectLanguageViewModel = selectLanguageViewModel
        binding.lifecycleOwner = this



        setUpView()
        setUpLanguageRecyclerView()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    openSetting()
                }
            })
    }

    private fun setUpView(){
        binding.materialToolbar.setNavigationOnClickListener {
            // perform whatever you want on back arrow click
            openSetting()
        }

        lifecycleScope.launch{
            selectNotificationSoundAdapter.selectedNotificationSound = protoManager.fetchInitialPreferences().notificationRingtone
        }

    }

    private fun setUpLanguageRecyclerView(){
        binding.languageRecyclerView.apply {
            this.layoutManager = LinearLayoutManager(this.context)
            this.setHasFixedSize(true)
            adapter = selectNotificationSoundAdapter
        }

        selectNotificationSoundAdapter.notificationSoundList =selectLanguageViewModel.selectNotificationSoundList
        selectNotificationSoundAdapter.notificationSoundValue = selectLanguageViewModel.selectNotificationSoundValue

        selectLanguageViewModel.isSelectNotificationSoundProgress.value = false
        selectNotificationSoundAdapter.notifyItemRangeChanged(0, selectLanguageViewModel.selectNotificationSoundValue.size)
    }

    fun onClickContinueButton(){
        lifecycleScope.launch {
            protoManager.setNotificationRingtone(selectNotificationSoundAdapter.selectedNotificationSound)
        }

        openSetting()
    }


    private fun openSetting(){
        val intent = Intent(requireContext(), SettingsActivity::class.java)
        startActivity(intent)
    }



}