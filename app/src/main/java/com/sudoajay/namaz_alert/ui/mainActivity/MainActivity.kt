package com.sudoajay.namaz_alert.ui.mainActivity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.core.os.bundleOf
import androidx.core.view.WindowInsetsControllerCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.NavHostFragment
import com.sudoajay.namaz_alert.R
import com.sudoajay.namaz_alert.databinding.ActivityMainBinding
import com.sudoajay.namaz_alert.ui.BaseActivity
import com.sudoajay.namaz_alert.ui.BaseFragment
import com.sudoajay.namaz_alert.ui.background.WorkMangerForTask
import com.sudoajay.namaz_alert.ui.setting.SettingsActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity() {
    private var isDarkTheme: Boolean = false

    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isDarkTheme = isSystemDefaultOn(resources)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!isDarkTheme) {
                WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars =
                    true
            }

        }
        Log.e("MainClass", "Its is here  ${intent.getStringExtra(WorkMangerForTask.prayerTimeID)}")
        if (!intent.action.isNullOrEmpty() && intent.action.toString() == settingShortcutId) {
            openSetting()
        }
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        if (intent.getStringExtra(openMainActivityID) == settingShortcutId) {
            openSetting()
        } else if (intent.getStringExtra(WorkMangerForTask.prayerNameID)?.isNotEmpty() == true) {
            openSpecificEditPrayer()
        }
    }

    private fun openSpecificEditPrayer() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        navController.navigate(
            R.id.action_homeFragment_to_editDailyPrayerFragment,
            bundleOf(
                BaseFragment.editDailyPrayerNameKey to intent.getStringExtra(WorkMangerForTask.prayerNameID),
                BaseFragment.editDailyPrayerTimeKey to intent.getStringExtra(WorkMangerForTask.prayerTimeID)
            )
        )
    }

    private fun openSetting() {
        val intent = Intent(applicationContext, SettingsActivity::class.java)
        startActivity(intent)
    }


}
