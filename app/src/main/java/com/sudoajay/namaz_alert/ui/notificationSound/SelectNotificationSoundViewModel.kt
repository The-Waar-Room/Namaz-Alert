package com.sudoajay.namaz_alert.ui.notificationSound

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.sudoajay.namaz_alert.R
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SelectNotificationSoundViewModel @Inject constructor(application: Application) :
    AndroidViewModel(application) {

    var isSelectNotificationSoundProgress: MutableLiveData<Boolean> = MutableLiveData()
    var selectNotificationSoundList = mutableListOf<String>()


    init {
        selectNotificationSoundList =
            application.resources.getStringArray(R.array.setNotificationSound).toMutableList()
        loadHideProgress()
    }

    private fun loadHideProgress() {
        isSelectNotificationSoundProgress.value = true
    }

}