package com.sudoajay.namaz_alert.data.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sudoajay.namaz_alert.data.pojo.DailyPrayerAbdulrcs

@Dao
interface DailyPrayerAbdulrcsDoa {

    @Query("DELETE FROM DailyPrayerAbdulrcsDBTable")
    suspend fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(users: List<DailyPrayerAbdulrcsDB>)

    @Query("SELECT * FROM DailyPrayerAbdulrcsDBTable ")
    fun pagingSource(): PagingSource<Int, DailyPrayerAbdulrcsDB>



}