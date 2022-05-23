package com.sudoajay.namaz_alert.data.repository

import androidx.paging.PagingSource
import com.sudoajay.namaz_alert.data.db.DailyPrayerDB
import com.sudoajay.namaz_alert.data.db.DailyPrayerDoa

class DailyPrayerRepository(private val dailyPrayerDoa: DailyPrayerDoa) {

    suspend fun insertAll(list: List<DailyPrayerDB>) = dailyPrayerDoa.insertAll(list)

    suspend fun  deleteAll() = dailyPrayerDoa.deleteAll()

    fun pagingSource(searchDate:String,search: String): PagingSource<Int, DailyPrayerDB> = dailyPrayerDoa.pagingSource(searchDate ,search)

    fun listSource(searchDate:String,search: String): List< DailyPrayerDB> = dailyPrayerDoa.listSource(searchDate ,search)

    fun getNextTime(searchDate:String,currentTime: String) : DailyPrayerDB = dailyPrayerDoa.getNextTime(searchDate ,currentTime)


}