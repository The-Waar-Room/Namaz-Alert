package com.sudoajay.namaz_alert.ui.mainActivity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.core.os.bundleOf
import androidx.core.view.WindowInsetsControllerCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.sudoajay.namaz_alert.R
import com.sudoajay.namaz_alert.data.db.DailyPrayerDatabase
import com.sudoajay.namaz_alert.data.repository.DailyPrayerRepository
import com.sudoajay.namaz_alert.databinding.ActivityMainBinding
import com.sudoajay.namaz_alert.ui.BaseActivity
import com.sudoajay.namaz_alert.ui.BaseFragment
import com.sudoajay.namaz_alert.ui.background.AlarmMangerForTask
import com.sudoajay.namaz_alert.ui.setting.SettingsActivity
import com.sudoajay.namaz_alert.util.Helper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*

@AndroidEntryPoint
class MainActivity : BaseActivity() {
    private var isDarkTheme: Boolean = false

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    var isPermissionAsked = false

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
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        Log.e("MainClass", "Its is here  ${intent.getStringExtra(AlarmMangerForTask.prayerTimeID)}")
        if (!intent.action.isNullOrEmpty()) {
            when (intent.action.toString()) {
                vibrateModeID -> openSetting()
                notificationSoundID -> openSelectRingtone()
                nextPrayerID -> openNextPrayer()
                else -> {}
            }

        }
        Log.e("MainClass", "Its is hereasd   ${intent.getStringExtra(receiverId)}")

        if (intent.getStringExtra(receiverId) == notificationCancelReceiver) {
            Log.e("MainClass", "Its is hereasd   ${intent.getStringExtra(receiverId)}")
//            Helper.setWorkMangerRunning(protoManager, applicationContext, false)
            CoroutineScope(Dispatchers.IO).launch {
                workManger.startWorker()
            }
        }

        if (intent.getStringExtra(openMainActivityID) == settingShortcutId) {
            openSetting()
        } else if (intent.getStringExtra(AlarmMangerForTask.prayerNameID)?.isNotEmpty() == true) {
            openSpecificEditPrayer()
        } else if (intent.getStringExtra(openMainActivityID) == openSelectLanguageID) {
            openSelectLanguage()
        } else if (intent.getStringExtra(openMainActivityID) == openSelectNotificationSoundID) {
            openSelectRingtone()
        }

        isPermissionAsked = Helper.IsPermissionAsked(applicationContext)
        showPermissionAskedDrawer()





    }



    private fun openSpecificEditPrayer(prayerName:String? =null , prayerTime:String?=null) {
        navController.navigate(
            R.id.action_homeFragment_to_editDailyPrayerFragment,
            bundleOf(
                BaseFragment.editDailyPrayerNameKey to if(prayerName.isNullOrEmpty())
                    intent.getStringExtra(AlarmMangerForTask.prayerNameID) else prayerName,
                BaseFragment.editDailyPrayerTimeKey to if(prayerTime.isNullOrEmpty()) intent.getStringExtra(AlarmMangerForTask.prayerTimeID)
            else prayerTime
            )
        )
    }

    private fun openSetting() {
        val intent = Intent(applicationContext, SettingsActivity::class.java)
        startActivity(intent)
    }


    private fun openSelectLanguage() {
        navController.navigate(
            R.id.action_homeFragment_to_selectLanguageFragment
        )
    }

    private fun openSelectRingtone() {
        navController.navigate(
            R.id.action_homeFragment_to_selectRingtoneFragment
        )
    }

    private fun openNextPrayer() {
        CoroutineScope(Dispatchers.IO).launch {
            val dailyPrayerRepository =
                DailyPrayerRepository(
                    DailyPrayerDatabase.getDatabase(applicationContext).dailyPrayerDoa()
                )
            val currentTime = Helper.getCurrentTime()
            val dailyPrayerDB =
                dailyPrayerRepository.getNextTime(Helper.getTodayDate(), Helper.getTomorrowDate() , currentTime)
            withContext(Dispatchers.Main) {
                openSpecificEditPrayer(dailyPrayerDB.Name,dailyPrayerDB.Time)
            }
        }
    }

    private fun showPermissionAskedDrawer() {
        if (!isPermissionAsked && !(Helper.doNotDisturbPermissionAlreadyGiven(applicationContext)))
            notDisturbPermissionBottomSheet.show(
                supportFragmentManager.beginTransaction(),
                notDisturbPermissionBottomSheet.tag
            )

    }






}
