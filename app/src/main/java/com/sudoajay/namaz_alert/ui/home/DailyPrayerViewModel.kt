package com.sudoajay.namaz_alert.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.sudoajay.namaz_alert.data.db.DailyPrayerDB
import com.sudoajay.namaz_alert.data.db.DailyPrayerDatabase
import com.sudoajay.namaz_alert.data.network.DailyPrayerApiInterface.Companion.NETWORK_PAGE_SIZE
import com.sudoajay.namaz_alert.data.repository.DailyPrayerRepository
import com.sudoajay.namaz_alert.util.Helper.Companion.getTodayDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import java.util.*
import javax.inject.Inject

@HiltViewModel
class DailyPrayerViewModel @Inject constructor(application: Application) : AndroidViewModel(application) {
    private var viewModelApplication = application
    var searchValue :String = ""





    fun getPagingGsonSourceWithNetwork(): Flow<PagingData<DailyPrayerDB>> {
        val database = DailyPrayerDatabase.getDatabase(viewModelApplication)
        val dailyPrayerRepository = DailyPrayerRepository(database.dailyPrayerDoa())

        return Pager(
            config = PagingConfig(pageSize = NETWORK_PAGE_SIZE, enablePlaceholders = false),
            pagingSourceFactory = {
               dailyPrayerRepository.pagingSource(getTodayDate(),searchValue)
            }
        ).flow
    }




//    fun getDataFromApi(){
//        val apiInterface =
//            getApiInterface()
//        getGsonApi(apiInterface?.getAllCharacter("India"))
//
//        Log.e("SomethingNew", "here come getDataFromApi ")
//
//    }
//
//
//    private fun getGsonApi(call: Call<DailyPrayer>?) {
//        call?.enqueue(object : Callback<DailyPrayer> {
//
//            override fun onResponse(
//                call: Call<DailyPrayer>,
//                response: Response<DailyPrayer>
//            ) {
//                Log.e("SomethingNew", "hereee")
//
//
//                Log.e("SomethingNew", response.body().toString()+"  -  " + response.message() + " -- " +
//                        response.errorBody().toString())
//
//
//
//            }
//
//            override fun onFailure(call: Call<DailyPrayer>, t: Throwable) {
//                Toaster.showToast( viewModelApplication.applicationContext,
//                    "Something wen wrong")
//                Log.e("SomethingNew", "  Something wen wrong  " + t.message + " " + t.localizedMessage)
//            }
//        })
//    }

}