package com.sudoajay.triumph_path.ui.notification

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.sudoajay.triumph_path.R
import com.sudoajay.triumph_path.databinding.FragmentNotificationPermissionBinding
import com.sudoajay.triumph_path.ui.BaseActivity
import com.sudoajay.triumph_path.ui.BaseFragment
import com.sudoajay.triumph_path.util.Helper
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class NotificationPermissionFragment : BaseFragment() {
    private var isDarkTheme: Boolean = false

    private var _binding: FragmentNotificationPermissionBinding? = null
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        isDarkTheme = BaseActivity.isSystemDefaultOn(resources)


        requireActivity().changeStatusBarColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.bgBoxColor
            ), !isDarkTheme
        )


        _binding = FragmentNotificationPermissionBinding.inflate(inflater, container, false)
        binding?.lifecycleOwner = this
        binding?.fragment = this



        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    onClickSkip()
                }
            })
    }


    fun onClickSkip() {
        openHomeActivity()
    }

    fun onClickGetNotify() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            pushNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    private fun openHomeActivity() {
        Helper.setIsNotificationPermissionAsked(requireContext(), true)
        findNavController().navigate(R.id.action_navigation_firebasePushNotificationFragment_to_homeFragment)
    }


    private val pushNotificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (!isGranted)
            throwToaster(getString(R.string.permission_not_granted_by_the_user_text))

        openHomeActivity()
    }


    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }
}
