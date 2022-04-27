package com.sudoajay.namaz_alert.ui.mainActivity

import android.os.Build
import android.os.Bundle
import androidx.core.view.WindowInsetsControllerCompat
import androidx.databinding.DataBindingUtil
import com.sudoajay.namaz_alert.R
import com.sudoajay.namaz_alert.databinding.ActivityMainBinding
import com.sudoajay.namaz_alert.ui.BaseActivity


class MainActivity : BaseActivity() {
    private var isDarkTheme: Boolean = false

    private lateinit var binding:ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isDarkTheme = isSystemDefaultOn(resources)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!isDarkTheme) {
                WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars =
                    true
            }

        }


        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)


//        val nm:NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M && !nm.isNotificationPolicyAccessGranted){
//            startActivity(Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS))
//        }
//
//        val am = getSystemService(Context.AUDIO_SERVICE) as AudioManager
//        am.ringerMode = AudioManager.RINGER_MODE_SILENT
    }


}
