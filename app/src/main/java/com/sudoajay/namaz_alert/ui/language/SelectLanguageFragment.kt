package com.sudoajay.namaz_alert.ui.language

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityCompat.recreate
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.sudoajay.namaz_alert.R
import com.sudoajay.namaz_alert.databinding.FragmentSelectLanguageBinding
import com.sudoajay.namaz_alert.ui.BaseActivity
import com.sudoajay.namaz_alert.ui.BaseFragment
import com.sudoajay.namaz_alert.ui.language.repository.SelectLanguageAdapter
import com.sudoajay.namaz_alert.ui.mainActivity.MainActivity
import com.sudoajay.namaz_alert.ui.setting.SettingsActivity
import com.sudoajay.namaz_alert.util.Helper
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SelectLanguageFragment : BaseFragment() {

    private var _binding: FragmentSelectLanguageBinding? = null
    private val binding get() = _binding!!

    private val selectLanguageViewModel: SelectLanguageViewModel by viewModels()

    @Inject
    lateinit var selectLanguageAdapter: SelectLanguageAdapter
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
        _binding = FragmentSelectLanguageBinding.inflate(inflater, container, false)
        binding.selectLanguageViewModel = selectLanguageViewModel
        binding.lifecycleOwner = this
        binding.fragment = this

        selectLanguageAdapter.selectedLanguage = Helper.getLanguage(requireContext())

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

    }

    private fun setUpLanguageRecyclerView() {
        binding.languageRecyclerView.apply {
            this.layoutManager = LinearLayoutManager(this.context)
            this.setHasFixedSize(true)
            adapter = selectLanguageAdapter
        }
        selectLanguageAdapter.languageNonChangeList =
            selectLanguageViewModel.selectLanguageNonChangeList
        selectLanguageAdapter.languageList = requireContext().resources.getStringArray(R.array.languagesList).toMutableList()
        selectLanguageAdapter.languageValue = selectLanguageViewModel.selectLanguageValue
        selectLanguageViewModel.isSelectLanguageProgress.value = false
        selectLanguageAdapter.notifyItemRangeChanged(
            0,
            selectLanguageViewModel.selectLanguageNonChangeList.size
        )
    }


    private fun openSetting() {
        val intent = Intent(requireContext(), SettingsActivity::class.java)
        startActivity(intent)
    }

    fun onClickContinueButton() {
        Helper.setLanguage(requireContext(), selectLanguageAdapter.selectedLanguage)
        val intent = Intent(requireContext(), MainActivity::class.java)
        intent.putExtra(BaseActivity.openMainActivityID, BaseActivity.openSelectLanguageID)
        startActivity(intent)
    }


}