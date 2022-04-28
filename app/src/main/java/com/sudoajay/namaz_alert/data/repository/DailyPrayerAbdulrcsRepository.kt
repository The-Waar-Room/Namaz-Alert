package com.sudoajay.namaz_alert.data.repository

import androidx.paging.PagingSource
import com.sudoajay.namaz_alert.data.db.DailyPrayerAbdulrcsDB
import com.sudoajay.namaz_alert.data.db.DailyPrayerAbdulrcsDoa

class DailyPrayerAbdulrcsRepository(private val dailyPrayerAbdulrcsDoa: DailyPrayerAbdulrcsDoa) {

    suspend fun insertAll(list: List<DailyPrayerAbdulrcsDB>) = dailyPrayerAbdulrcsDoa.insertAll(list)

    suspend fun  deleteAll() = dailyPrayerAbdulrcsDoa.deleteAll()

    fun pagingSource(): PagingSource<Int, DailyPrayerAbdulrcsDB> = dailyPrayerAbdulrcsDoa.pagingSource()


}