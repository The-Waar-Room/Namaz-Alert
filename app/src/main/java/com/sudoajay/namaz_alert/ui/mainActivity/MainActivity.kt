package com.sudoajay.namaz_alert.ui.mainActivity

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.core.view.WindowInsetsControllerCompat
import androidx.databinding.DataBindingUtil
import com.sudoajay.namaz_alert.R
import com.sudoajay.namaz_alert.databinding.ActivityMainBinding
import com.sudoajay.namaz_alert.ui.BaseActivity
import com.sudoajay.namaz_alert.ui.setting.SettingsActivity
import com.sudoajay.namaz_alert.util.Command
import com.sudoajay.namaz_alert.util.Toaster
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
        if (!intent.action.isNullOrEmpty() && intent.action.toString() == settingShortcutId) {
            openSetting()
        }

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)






    }


    private fun openSetting(){
        val intent = Intent(applicationContext, SettingsActivity::class.java)
        startActivity(intent)
    }



}
