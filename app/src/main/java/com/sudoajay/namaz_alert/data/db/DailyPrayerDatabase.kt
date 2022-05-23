package com.sudoajay.namaz_alert.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [DailyPrayerDB::class], version = 1, exportSchema = false)
abstract class DailyPrayerDatabase : RoomDatabase() {

    abstract fun dailyPrayerDoa(): DailyPrayerDoa

    companion object {
        @Volatile
        private var INSTANCE: DailyPrayerDatabase? = null

        fun getDatabase(
            context: Context
        ): DailyPrayerDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DailyPrayerDatabase::class.java,
                    "DailyPrayerTable_database"
                ).fallbackToDestructiveMigration()

                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}