package com.sudoajay.namaz_alert.ui.language

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.sudoajay.namaz_alert.R
import com.sudoajay.namaz_alert.model.Language
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SelectLanguageViewModel @Inject constructor(application: Application) : AndroidViewModel(application) {

    var isSelectLanguageProgress: MutableLiveData<Boolean> = MutableLiveData()
    var selectLanguageList = mutableListOf<String>()
    var selectLanguageValue = mutableListOf<String>()


    init {
        selectLanguageList =  application.resources.getStringArray(R.array.languageList).toMutableList()
        selectLanguageValue = application.resources.getStringArray(R.array.languagesValues).toMutableList()
        loadHideProgress()
    }
    private fun loadHideProgress() {
        isSelectLanguageProgress.value = true
    }

}