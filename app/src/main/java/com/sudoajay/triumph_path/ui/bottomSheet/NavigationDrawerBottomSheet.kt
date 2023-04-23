package com.sudoajay.triumph_path.ui.bottomSheet

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sudoajay.triumph_path.R
import com.sudoajay.triumph_path.databinding.LayoutNavigationDrawerBottomSheetBinding
import com.sudoajay.triumph_path.ui.feedbackAndHelp.SendFeedbackAndHelp
import com.sudoajay.triumph_path.util.Helper.Companion.getPackageInfoCompat

import javax.inject.Inject


class NavigationDrawerBottomSheet @Inject constructor() : BottomSheetDialogFragment() {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val myDrawerView =
            layoutInflater.inflate(R.layout.layout_navigation_drawer_bottom_sheet, null)
        val binding = LayoutNavigationDrawerBottomSheetBinding.inflate(
            layoutInflater,
            myDrawerView as ViewGroup,
            false
        )
        binding.navigation = this

        binding.aboutMeTextView.text =
            getString(R.string.developer_name, getString(R.string.team_Name))

        return binding.root
    }





    fun sendFeedback() {
        dismiss()
        startActivity(Intent(requireContext(), SendFeedbackAndHelp::class.java))
    }



    fun developerPage() {
        dismiss()
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(getString(R.string.moreApp_link_text))
        startActivity(i)
    }

    fun getVersionName(): String {
        var versionName = "1.0.0"
        try {
            versionName = requireContext().packageManager.getPackageInfoCompat(requireContext().packageName, 0).versionName.toString()
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return "%s %s".format(getString(R.string.app_version_text), versionName)
    }










}



