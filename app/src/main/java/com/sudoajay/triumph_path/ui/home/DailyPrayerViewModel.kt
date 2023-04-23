package com.sudoajay.triumph_path.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.sudoajay.triumph_path.data.db.DailyPrayerDB
import com.sudoajay.triumph_path.data.db.DailyPrayerDatabase
import com.sudoajay.triumph_path.data.repository.DailyPrayerRepository
import com.sudoajay.triumph_path.util.Helper.Companion.getTodayDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class DailyPrayerViewModel @Inject constructor(application: Application) :
    AndroidViewModel(application) {
    private var viewModelApplication = application
    var isLoadData: MutableLiveData<Boolean> = MutableLiveData()

    private var searchValue: String = ""
    private var NETWORK_PAGE_SIZE = 5

    init {
        loadData()

    }


    fun getPagingGsonSourceWithNetwork(): Flow<PagingData<DailyPrayerDB>> {
        val database = DailyPrayerDatabase.getDatabase(viewModelApplication)
        val dailyPrayerRepository = DailyPrayerRepository(database.dailyPrayerDoa())

        return Pager(
            config = PagingConfig(pageSize = NETWORK_PAGE_SIZE, enablePlaceholders = false),
            pagingSourceFactory = {
                dailyPrayerRepository.pagingSource(getTodayDate(), searchValue)
            }
        ).flow
    }

    private fun loadData() {
        isLoadData.value = true
    }


}