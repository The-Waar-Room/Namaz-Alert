package com.sudoajay.namaz_alert.data.repository

import android.content.Context
import android.util.Log
import com.sudoajay.namaz_alert.data.db.DailyPrayerDB
import com.sudoajay.namaz_alert.data.db.DailyPrayerDatabase
import com.sudoajay.namaz_alert.ui.BaseFragment
import com.sudoajay.namaz_alert.ui.background.WorkMangerForTask
import com.sudoajay.namaz_alert.util.Helper.Companion.doesDatabaseExist
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class WebScrappingGoogle @Inject constructor(var context: Context) {

    private lateinit var database: DailyPrayerDatabase
    private lateinit var dailyPrayerRepository: DailyPrayerRepository


    lateinit var workManger: WorkMangerForTask

    fun checkEvertTimeIfDataIsUpdated() {
        workManger = WorkMangerForTask(context)

        database = DailyPrayerDatabase.getDatabase(context)
        dailyPrayerRepository = DailyPrayerRepository(database.dailyPrayerDoa())
        CoroutineScope(Dispatchers.IO).launch {
            var getDate =""
            if (doesDatabaseExist(context, dailyPrayerRepository)) {
                getDate = dailyPrayerRepository.getIndexDate()
            }
            val todayDate =
                SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH).format(Calendar.getInstance().time)
            if (getDate != todayDate) {
                getData()
            }

        }
    }



    private suspend fun getData() {
        val formatDate = SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH)
        val cal = Calendar.getInstance()
        val currentDay = cal.get(Calendar.DAY_OF_MONTH)
        val currentMonth = SimpleDateFormat("MMM", Locale.ENGLISH).format(cal.time)
        val todayDate = formatDate.format(cal.time)
        cal.add(Calendar.DAY_OF_MONTH, 1)
        val nextDate = formatDate.format(cal.time)
        cal.add(Calendar.DAY_OF_MONTH, 1)
        val nextDay = cal.get(Calendar.DAY_OF_MONTH)
        val nextMonth = SimpleDateFormat("MMM", Locale.ENGLISH).format(cal.time)

        Log.e("GetData", " $currentDay - $currentMonth  $nextDay  -$nextMonth")


        var doc: Document? = null
        val result = kotlin.runCatching {
            doc = Jsoup.connect("https://www.google.com/search?q=Islamic+prayer+times").get()
        }
        if (result.isSuccess) {
            Log.e("GetData", "success is on your side")
        } else {
            Log.e("GetData", "one failure is never the end")
        }
        val allInfo = doc?.getElementsByClass("bvYrdf")
        Log.e("GetData", allInfo.toString())
        val getExactString =
            Regex(pattern = "$currentDay $currentMonth([\\s\\S]*?)$nextDay $nextMonth")
                .find(allInfo.toString())?.value
        Log.e("GetData", getExactString.toString())

        val getAllTime = Regex(pattern = "(\\d\\d):(\\d\\d)").findAll(getExactString.toString())
        val list = mutableListOf<DailyPrayerDB>()
        getAllTime.forEachIndexed { index, it ->
            if (index != 1 && index != 7) {
                Log.e("GetData", " value - $index ${it.value}")
            }
            when (index) {
                0 -> list.add(DailyPrayerDB(0, todayDate, BaseFragment.fajrName, it.value))
                2 -> list.add(DailyPrayerDB(1, todayDate, BaseFragment.dhuhrName, it.value))
                3 -> list.add(DailyPrayerDB(2, todayDate, BaseFragment.asrName, it.value))
                4 -> list.add(DailyPrayerDB(3, todayDate, BaseFragment.maghribName, it.value))
                5 -> list.add(DailyPrayerDB(4, todayDate, BaseFragment.ishaName, it.value))
                6 -> list.add(DailyPrayerDB(5, nextDate, BaseFragment.fajrName, it.value))
                8 -> list.add(DailyPrayerDB(6, nextDate, BaseFragment.dhuhrName, it.value))
                9 -> list.add(DailyPrayerDB(7, nextDate, BaseFragment.asrName, it.value))
                10 -> list.add(DailyPrayerDB(8, nextDate, BaseFragment.maghribName, it.value))
                11 -> list.add(DailyPrayerDB(9, nextDate, BaseFragment.ishaName, it.value))
                else -> {}
            }
        }
        dailyPrayerRepository.insertAll(list)
        workManger.startWorker()
    }


}