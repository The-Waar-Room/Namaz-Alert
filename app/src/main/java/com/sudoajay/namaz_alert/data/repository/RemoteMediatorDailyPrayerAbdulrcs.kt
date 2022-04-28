package com.sudoajay.namaz_alert.data.repository

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.sudoajay.namaz_alert.data.db.DailyPrayerAbdulrcsDB
import com.sudoajay.namaz_alert.data.db.DailyPrayerAbdulrcsDatabase
import com.sudoajay.namaz_alert.data.network.DailyPrayerAbdulrcslApiInterface
import com.sudoajay.namaz_alert.data.pojo.DailyPrayerAbdulrcs
import retrofit2.HttpException
import java.io.IOException
import java.lang.Exception
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalPagingApi::class)
class RemoteMediatorDailyPrayerAbdulrcs(
    private  val location:String? = "India" ,
    private val database: DailyPrayerAbdulrcsDatabase,
    private val dailyPrayerAbdulrcsRepository: DailyPrayerAbdulrcsRepository,
    private val dailyPrayerAbdulrcslApiInterface: DailyPrayerAbdulrcslApiInterface
) : RemoteMediator<Int, DailyPrayerAbdulrcsDB>() {

//    override suspend fun initialize(): InitializeAction {
//        Log.e("SomethingNew", "Come initialize here")
//        val cacheTimeout = TimeUnit.HOURS.convert(1, TimeUnit.MILLISECONDS)
//        return if (System.currentTimeMillis() - userDao.lastUpdated() >= cacheTimeout) {
//            // Cached data is up-to-date, so there is no need to re-fetch from network.
//            InitializeAction.SKIP_INITIAL_REFRESH
//        } else {
//            // Need to refresh cached data from network; returning LAUNCH_INITIAL_REFRESH here
//            // will also block RemoteMediator's APPEND and PREPEND from running until REFRESH
//            // succeeds.
//            InitializeAction.LAUNCH_INITIAL_REFRESH
//        }
//        return InitializeAction.SKIP_INITIAL_REFRESH
//    }


    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, DailyPrayerAbdulrcsDB>
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
            val response = dailyPrayerAbdulrcslApiInterface.getAllCharacters(location)
            Log.e("SomethingNew", "Come here")
            // Store loaded data, and next key in transaction, so that they're always consistent
            database.withTransaction {
                val list = listOf(
                    DailyPrayerAbdulrcsDB(0,response.city,response.date,response.today.Fajr,
                        response.today.Sunrise,response.today.Dhuhr , response.today.Asr,response.today.Maghrib,response.today.Ishaa
                    ),
                    DailyPrayerAbdulrcsDB(0,response.city,response.date,response.tomorrow.Fajr,
                        response.tomorrow.Sunrise,response.tomorrow.Dhuhr , response.tomorrow.Asr,response.tomorrow.Maghrib,response.tomorrow.Ishaa
                    ))

                list.forEach {
                    Log.e("SomethingNew", it.toString())
                }
                dailyPrayerAbdulrcsRepository.insertAll(list)

            }

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