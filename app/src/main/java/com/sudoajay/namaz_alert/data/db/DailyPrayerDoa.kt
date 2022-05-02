package com.sudoajay.namaz_alert.data.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface DailyPrayerDoa {

    @Query("DELETE FROM DailyPrayerTable")
    suspend fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(users: List<DailyPrayerDB>)

    @Query("SELECT * FROM DailyPrayerTable WHERE Date is :searchDate")
    fun pagingSource(searchDate:String): PagingSource<Int, DailyPrayerDB>



}