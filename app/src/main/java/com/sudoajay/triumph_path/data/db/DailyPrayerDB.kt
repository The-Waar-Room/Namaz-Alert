package com.sudoajay.triumph_path.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "DailyPrayerTable")
class DailyPrayerDB(
    @PrimaryKey @ColumnInfo("id") var id: Long?,
   @ColumnInfo("date") val Date: String,
   @ColumnInfo("name") val Name: String,
   @ColumnInfo("time") val Time: String
)