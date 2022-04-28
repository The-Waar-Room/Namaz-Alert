package com.sudoajay.namaz_alert.ui.home

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.sudoajay.namaz_alert.data.db.DailyPrayerAbdulrcsDB
import com.sudoajay.namaz_alert.data.db.DailyPrayerAbdulrcsDatabase
import com.sudoajay.namaz_alert.data.network.DailyPrayerAbdulrcsBuilder
import com.sudoajay.namaz_alert.data.network.DailyPrayerAbdulrcslApiInterface
import com.sudoajay.namaz_alert.data.network.DailyPrayerAbdulrcslApiInterface.Companion.NETWORK_PAGE_SIZE
import com.sudoajay.namaz_alert.data.pojo.DailyPrayerAbdulrcs
import com.sudoajay.namaz_alert.data.repository.DailyPrayerAbdulrcsRepository
import com.sudoajay.namaz_alert.data.repository.RemoteMediatorDailyPrayerAbdulrcs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class DailyPrayerAbdulrcsViewModel @Inject constructor(application: Application) : AndroidViewModel(application) {
    var _application = application

    @OptIn(ExperimentalPagingApi::class)
    fun getRemoteMediatorWithDataBase(): Flow<PagingData<DailyPrayerAbdulrcsDB>> {
        val database = DailyPrayerAbdulrcsDatabase.getDatabase(_application)
        val itemRepository = DailyPrayerAbdulrcsRepository(database.itemDoa())

        val apiInterface =
            DailyPrayerAbdulrcsBuilder.getApiInterface()
        Log.e("SomethingNew", "Come getRemoteMediatorWithDataBase here")

        return Pager(
            config = PagingConfig(pageSize = NETWORK_PAGE_SIZE),
            remoteMediator = RemoteMediatorDailyPrayerAbdulrcs(location = "India",
                database=database,
                dailyPrayerAbdulrcsRepository= itemRepository,
                dailyPrayerAbdulrcslApiInterface= apiInterface!!
            )
        ) {
            itemRepository.pagingSource()
        }.flow
    }

}