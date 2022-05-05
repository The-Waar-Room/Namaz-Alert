package com.sudoajay.namaz_alert.ui

import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import com.sudoajay.namaz_alert.util.Toaster
import com.sudoajay.namaz_alert.util.proto.ProtoManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
open class BaseActivity : AppCompatActivity() {

    @Inject
    lateinit var protoManager: ProtoManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSystemDefaultOn()
        getDataFromProtoDatastore()
    }

    private fun setSystemDefaultOn() {
        AppCompatDelegate.setDefaultNightMode(
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        )
    }
    private fun getDataFromProtoDatastore() {
        protoManager.dataStoreStatePreferences.data.asLiveData().observe(this) {
            lifecycleScope.launch {
                if (it.setPhoneMode == "")
                    protoManager.setDefaultValue()
            }
        }

    }


    fun throwToaster(value: String?) {
        Toaster.showToast(applicationContext, value ?: "")
    }

    companion object {
        fun isSystemDefaultOn(resources: Resources): Boolean {
            return resources.configuration.uiMode and
                    Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
        }
        const val messageType= "MessageType"
        const val settingShortcutId = "setting"
        const val phoneModeShortcutId = "phoneMode"

    }



}