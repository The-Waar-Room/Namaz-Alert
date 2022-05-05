package com.sudoajay.namaz_alert.data.repository

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.sudoajay.namaz_alert.data.db.DailyPrayerDB
import com.sudoajay.namaz_alert.data.db.DailyPrayerDatabase
import com.sudoajay.namaz_alert.data.network.DailyPrayerApiInterface
import com.sudoajay.namaz_alert.ui.BaseFragment.Companion.asrName
import com.sudoajay.namaz_alert.ui.BaseFragment.Companion.dhuhrName
import com.sudoajay.namaz_alert.ui.BaseFragment.Companion.fajrName
import com.sudoajay.namaz_alert.ui.BaseFragment.Companion.ishaName
import com.sudoajay.namaz_alert.ui.BaseFragment.Companion.maghribName
import retrofit2.HttpException
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


@OptIn(ExperimentalPagingApi::class)
class DailyPrayerRemoteMediator(
    private  val location:String? = "India",
    private val database: DailyPrayerDatabase,
    private val dailyPrayerRepository: DailyPrayerRepository,
    private val dailyPrayerApiInterface: DailyPrayerApiInterface
) : RemoteMediator<Int, DailyPrayerDB>() {

    override suspend fun initialize(): InitializeAction {
        Log.e("SomethingNew", "Come initialize here")

        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }


        override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, DailyPrayerDB>
    ): MediatorResult {
        Log.e("SomethingNew", "Come now here 1234")

        return try {
            Log.v("SomethingNew", "Come  hello here")

            // The network load method takes an optional [String] parameter. For every page
            // after the first, we pass the [String] token returned from the previous page to
            // let it continue from where it left off. For REFRESH, pass `null` to load the
            // first page.
            val loadKey = when (loadType) {
                LoadType.REFRESH -> null
                // In this example, we never need to prepend, since REFRESH will always load the
                // first page in the list. Immediately return, reporting end of pagination.
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                // Query remoteKeyDao for the next RemoteKey.
                LoadType.APPEND -> return MediatorResult.Success(endOfPaginationReached = true)
            }


            // Suspending network load via Retrofit. This doesn't need to be wrapped in a
            // withContext(Dispatcher.IO) { ... } block since Retrofit's Coroutine CallAdapter
            // dispatches on a worker thread.
            val response = dailyPrayerApiInterface.getAllCharacters(location)
            Log.e("SomethingNew", "Come here")
            // Store loaded data, and next key in transaction, so that they're always consistent
            database.withTransaction {
                val cal = Calendar.getInstance()
                val todayDate = SimpleDateFormat("EEE,d MMM yyyy", Locale.ENGLISH).parse(response.date)
                cal.time = Date() // sets calendar time/date
                cal.add(Calendar.DAY_OF_MONTH, 1)      // adds one day
                val nextDate = cal.time
                val formatDate = SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH)


                val list = listOf(
                    DailyPrayerDB(0,response.city,formatDate.format(todayDate!!),fajrName,response.today.Fajr),
                    DailyPrayerDB(1,response.city,formatDate.format(todayDate), dhuhrName,response.today.Dhuhr),
                    DailyPrayerDB(2,response.city,formatDate.format(todayDate), asrName,response.today.Asr),
                    DailyPrayerDB(3,response.city,formatDate.format(todayDate),maghribName,response.today.Maghrib),
                    DailyPrayerDB(4,response.city,formatDate.format(todayDate),ishaName,response.today.Ishaa),
                    DailyPrayerDB(5,response.city,formatDate.format(nextDate),fajrName,response.tomorrow.Fajr),
                    DailyPrayerDB(6,response.city,formatDate.format(nextDate),dhuhrName,response.tomorrow.Dhuhr),
                    DailyPrayerDB(7,response.city,formatDate.format(nextDate),asrName,response.tomorrow.Asr),
                    DailyPrayerDB(8,response.city,formatDate.format(nextDate),maghribName,response.tomorrow.Maghrib),
                    DailyPrayerDB(9,response.city,formatDate.format(nextDate),ishaName,response.tomorrow.Ishaa)
                )

                list.forEach {
                    Log.e("SomethingNew", " today " + it.Date)
                }
                dailyPrayerRepository.insertAll(list)
            }
            Log.e("SomethingNew", " -- asdas  " )
            MediatorResult.Success(endOfPaginationReached = false)

        } catch (e: IOException) {
            Log.e("SomethingNew", " --  " )
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            Log.e("SomethingNew", " ++")
            MediatorResult.Error(e)
        } catch (e: Exception){
            Log.e("SomethingNew", "----")
            MediatorResult.Error(e)
        }
    }

}