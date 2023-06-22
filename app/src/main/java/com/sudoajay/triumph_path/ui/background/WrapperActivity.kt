package com.sudoajay.triumph_path.ui.background

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import com.sudoajay.triumph_path.R
import com.sudoajay.triumph_path.databinding.ActivityMainBinding
import com.sudoajay.triumph_path.databinding.ActivityWrapperBinding
import com.sudoajay.triumph_path.ui.BaseFragment
import com.sudoajay.triumph_path.util.Helper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class WrapperActivity : FragmentActivity() {

    private lateinit var binding: ActivityWrapperBinding

    private lateinit var dataShare: List<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dataShare = intent.getStringExtra(AlarmsScheduler.DATA_SHARE_ID).toString().split("||")



        turnScreenOn()
        updateLayout()


    }

    private fun turnScreenOn() {
        if (Build.VERSION.SDK_INT >= 27) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        }
        // Deprecated flags are required on some devices, even with API>=27
        window.addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        )

        CoroutineScope(Dispatchers.Main).launch {
            delay(1000 * 60)
            finish()

        }


    }


    private fun updateLayout() {

        binding = DataBindingUtil.setContentView(this, R.layout.activity_wrapper)

        changeStatusBarColor(
            ContextCompat.getColor(
                applicationContext,
                getColor()
            ), false
        )

        binding.mainConstraintLayout.background =
            ContextCompat.getDrawable(applicationContext, getColor())
        binding.itemBgImage.setImageResource(getDrawableImage())
        binding.view.background = ContextCompat.getDrawable(applicationContext, getDrawableView())


        binding.alertButtonOk.run {
            requestFocus()
            setOnClickListener {

                finish()
            }
            setOnLongClickListener {

                finish()
                true
            }
        }
        binding.alertButtonDismiss.run {
            setOnClickListener {
                text = getString(R.string.alarm_alert_hold_the_button_text)
            }
            setOnLongClickListener {
                sendBroadCast()
                finish()
                true
            }
        }

        setTitle()
        setTime()


    }

    fun Activity.changeStatusBarColor(color: Int, isLight: Boolean) {
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = color

        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = isLight


    }


    private fun setTitle() {
        val titleText = dataShare[0]
        title = titleText
       binding.alarmAlertLabel.text = titleText
    }

    private fun setTime() {
        binding.digitalClockTime.text =
            Helper.convertTo12HrOnly(Helper.getCurrentTime())
        binding.digitalClockAmPm.text =
            Helper.getAMOrPM(applicationContext, Helper.getCurrentTime())
    }

    private fun sendBroadCast() {
        val cancelIntent = Intent(applicationContext, BroadcastAlarmReceiver::class.java)
        cancelIntent.action = AlarmsScheduler.ACTION_CANCEL
        cancelIntent.putExtra(
            AlarmsScheduler.DATA_SHARE_ID,
            intent.getStringExtra(AlarmsScheduler.DATA_SHARE_ID).toString()
        )
        sendBroadcast(cancelIntent)
    }

    private fun getColor(): Int {
        return when (dataShare[0]) {
            BaseFragment.fajrName -> R.color.fajr_color
            BaseFragment.dhuhrName -> R.color.dhuhr_color
            BaseFragment.asrName -> R.color.asr_color
            BaseFragment.maghribName -> R.color.maghrib_color
            else -> R.color.isha_color
        }
    }

    private fun getDrawableView(): Int {
        return when (dataShare[0]) {
            BaseFragment.fajrName -> R.drawable.fajr_bg_gradient_drawable
            BaseFragment.dhuhrName -> R.drawable.dhuhr_bg_gradient_drawable
            BaseFragment.asrName -> R.drawable.asr_bg_gradient_drawable
            BaseFragment.maghribName -> R.drawable.maghrib_bg_gradient_drawable
            else -> R.drawable.isha_bg_gradient_drawable
        }
    }

    private fun getDrawableImage(): Int {
        return when (dataShare[0]) {
            BaseFragment.fajrName -> R.drawable.fajr_image
            BaseFragment.dhuhrName -> R.drawable.dhuhr_image
            BaseFragment.asrName -> R.drawable.asr_image
            BaseFragment.maghribName -> R.drawable.maghrib_image
            else -> R.drawable.isha_image
        }
    }

    /**
     * Interface for getting the instance of binder from our service class
     * So client can get instance of our service class and can directly communicate with it.
     */

}