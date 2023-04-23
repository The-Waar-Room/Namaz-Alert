package com.sudoajay.triumph_path.ui.notificationSound

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.sudoajay.triumph_path.R
import com.sudoajay.triumph_path.databinding.FragmentSelectNotificationSoundBinding
import com.sudoajay.triumph_path.ui.BaseActivity
import com.sudoajay.triumph_path.ui.BaseFragment
import com.sudoajay.triumph_path.ui.notificationSound.repository.SelectNotificationSoundAdapter
import com.sudoajay.triumph_path.ui.setting.SettingsActivity
import com.sudoajay.triumph_path.util.Helper
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SelectNotificationSoundFragment : BaseFragment() {

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
                R.color.appTheme
            ), false
        )
        // Inflate the layout for this fragment
        _binding = FragmentSelectNotificationSoundBinding.inflate(inflater, container, false)
        binding.selectLanguageViewModel = selectLanguageViewModel
        binding.fragment = this
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

    private fun setUpView() {
        binding.materialToolbar.setNavigationOnClickListener {
            // perform whatever you want on back arrow click
            openSetting()
        }


        selectNotificationSoundAdapter.selectedNotificationSound =
            Helper.getNotificationRingtone(requireContext())
    }

    private fun setUpLanguageRecyclerView() {
        binding.languageRecyclerView.apply {
            this.layoutManager = LinearLayoutManager(this.context)
            this.setHasFixedSize(true)
            adapter = selectNotificationSoundAdapter
        }

        selectNotificationSoundAdapter.notificationSoundList =
            selectLanguageViewModel.selectNotificationSoundList
        selectNotificationSoundAdapter.notificationSoundValue =
            requireContext().resources.getStringArray(R.array.setNotificationSoundValues)
                .toMutableList()

        selectLanguageViewModel.isSelectNotificationSoundProgress.value = false
        selectNotificationSoundAdapter.notifyItemRangeChanged(
            0,
            selectLanguageViewModel.selectNotificationSoundList.size
        )
    }

    fun onClickContinueButton() {

        Helper.setNotificationRingtone(
            requireContext(),
            selectNotificationSoundAdapter.selectedNotificationSound
        )
        Log.e("WorkManger", " getTAg " + selectNotificationSoundAdapter.selectedNotificationSound)

        openSetting()
    }


    private fun openSetting() {
        stopSound()
        val intent = Intent(requireContext(), SettingsActivity::class.java)
        startActivity(intent)
    }

    private fun stopSound() {
        selectNotificationSoundAdapter.ringtone?.stop()
        selectNotificationSoundAdapter.mp?.stop()
    }
}