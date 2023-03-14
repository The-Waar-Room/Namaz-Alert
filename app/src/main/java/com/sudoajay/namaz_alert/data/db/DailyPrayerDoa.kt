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

//          @Query("SELECT * FROM DailyPrayerTable WHERE (name LIKE '%' || :search || '%') or (time LIKE '%' || :search || '%')")
    @Query("SELECT * FROM DailyPrayerTable WHERE ( date is :searchDate and name LIKE '%' || :search || '%'  ) or ( date is :searchDate and time LIKE '%' || :search || '%')")
    fun pagingSource(searchDate:String , search: String): PagingSource<Int, DailyPrayerDB>


//    @Query("SELECT * FROM DailyPrayerTable WHERE ( date is :tomorrowDate and name is 'Dhuhr' ) Limit 1 ")
//    fun getNextTime(tomorrowDate :String): DailyPrayerDB

    @Query("SELECT * FROM DailyPrayerTable WHERE ( date is :searchDate and time > :currentTime ) or ( date is :tomorrowDate and name is 'Fajr' ) or (id is (SELECT MAX(id) FROM DailyPrayerTable where  name is 'Fajr'))  Limit 1 ")
    fun getNextTime(searchDate:String ,tomorrowDate :String , currentTime: String): DailyPrayerDB


    @Query("SELECT Date FROM DailyPrayerTable WHERE id is :index ")
    fun getIndexDate(index:Int):String

    @Query("SELECT count(*) FROM DailyPrayerTable ")
    fun getCount():Int





}