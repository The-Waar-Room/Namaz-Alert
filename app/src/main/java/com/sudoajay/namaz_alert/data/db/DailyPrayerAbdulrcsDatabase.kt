package com.sudoajay.namaz_alert.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [DailyPrayerAbdulrcsDB::class], version = 1 , exportSchema = false)
abstract class DailyPrayerAbdulrcsDatabase : RoomDatabase() {

    abstract fun itemDoa(): DailyPrayerAbdulrcsDoa

    companion object {
        @Volatile
        private var INSTANCE: DailyPrayerAbdulrcsDatabase? = null

        fun getDatabase(
            context: Context
        ): DailyPrayerAbdulrcsDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DailyPrayerAbdulrcsDatabase::class.java,
                    "PersonGsonTable_database"
                )

                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}