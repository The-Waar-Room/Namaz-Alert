package com.sudoajay.triumph_path.data.repository

import androidx.paging.PagingSource
import com.sudoajay.triumph_path.data.db.DailyPrayerDB
import com.sudoajay.triumph_path.data.db.DailyPrayerDoa
import kotlinx.coroutines.flow.asFlow

class DailyPrayerRepository(private val dailyPrayerDoa: DailyPrayerDoa) {

    suspend fun insertAll(list: List<DailyPrayerDB>) = dailyPrayerDoa.insertAll(list)

    suspend fun  deleteAll() = dailyPrayerDoa.deleteAll()

    fun pagingSource(searchDate:String,search: String): PagingSource<Int, DailyPrayerDB> = dailyPrayerDoa.pagingSource(searchDate ,search)

    fun getAllData(searchDate:String): List<DailyPrayerDB> = dailyPrayerDoa.getAllData(searchDate)



    fun getNextTime(searchDate:String,tomorrowDate :String,currentTime: String) : DailyPrayerDB = dailyPrayerDoa.getNextTime( searchDate,tomorrowDate, currentTime)

    fun getIndexDate(index:Int = 0): String = dailyPrayerDoa.getIndexDate(index)

    fun getCount(): Int = dailyPrayerDoa.getCount()


}