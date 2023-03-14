package com.sudoajay.namaz_alert.ui.bottomSheet

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sudoajay.namaz_alert.R
import com.sudoajay.namaz_alert.data.proto.ProtoManager
import com.sudoajay.namaz_alert.databinding.LayoutDoNotDisturbPermissionBottomSheetBinding
import com.sudoajay.namaz_alert.util.Helper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
        Helper.setIsPermissionAsked(requireContext(),true)
    }


}

