package com.sudoajay.triumph_path.ui.bottomSheet

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sudoajay.triumph_path.R
import com.sudoajay.triumph_path.data.proto.ProtoManager
import com.sudoajay.triumph_path.databinding.LayoutDoNotDisturbPermissionBottomSheetBinding
import com.sudoajay.triumph_path.util.Helper
import javax.inject.Inject


class DoNotDisturbPermissionBottomSheet @Inject constructor() : BottomSheetDialogFragment() {

    lateinit var protoManager: ProtoManager
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val myDrawerView =
            layoutInflater.inflate(R.layout.layout_do_not_disturb_permission_bottom_sheet, null)
        val binding = LayoutDoNotDisturbPermissionBottomSheetBinding.inflate(
            layoutInflater,
            myDrawerView as ViewGroup,
            false
        )
        binding.bottomSheet = this

        protoManager = ProtoManager(requireContext())
        return binding.root
    }

    fun onClickNoThanks() {
        setValuePermission()
        dismiss()

    }

    fun onClickAllow() {
        dismiss()
        val nm: NotificationManager =
            requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !nm.isNotificationPolicyAccessGranted) {
            startActivity(Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS))
        }
        setValuePermission()
    }

    private fun setValuePermission() {
        Helper.setIsPermissionAsked(requireContext(), true)
    }


}

