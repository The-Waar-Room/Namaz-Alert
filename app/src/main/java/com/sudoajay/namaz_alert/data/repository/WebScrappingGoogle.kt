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
        var cal = Calendar.getInstance()
        val currentDay = cal.get(Calendar.DAY_OF_MONTH)
        val currentMonth = SimpleDateFormat("MMM", Locale.ENGLISH).format(cal.time)

        cal.add(Calendar.DAY_OF_MONTH, 5)
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
        val allInfo = doc?.getElementsByClass("Kp6KVb")
        val getExactString =
            Regex(pattern = "$currentDay $currentMonth([\\s\\S]*?)$nextDay $nextMonth")
                .find(allInfo.toString())?.value
        Log.e("GetData", getExactString.toString())

        val getAllTime = Regex(pattern = "(\\d\\d):(\\d\\d)").findAll(getExactString.toString())
        val list = mutableListOf<DailyPrayerDB>()
        var count =0L
        cal = Calendar.getInstance()
        getAllTime.forEachIndexed { index, it ->
            if (index%6 != 1 ) {
                val  date = formatDate.format(cal.time)
                list.add(DailyPrayerDB(count, date, getPrayerName(count), it.value))
                Log.e("GetData", " value - $index ${it.value}     list value - ${list[count.toInt()]}")
                count++
                if(count.toInt() % 5 == 0)cal.add(Calendar.DAY_OF_MONTH, 1)

            }

        }
        list.forEach {
            Log.e("GetData", " value - ${it.Date} - ${it.Name} ${it.id} ${it.Time}")
        }
        dailyPrayerRepository.insertAll(list)
        workManger.startWorker()
    }

    private fun getPrayerName(count: Long): String {
        return when (if(count < 5) count.toInt()  else (count  % 5).toInt()){
            0 -> BaseFragment.fajrName
            1->  BaseFragment.dhuhrName
            2->BaseFragment.asrName
            3->BaseFragment.maghribName
            else -> BaseFragment.ishaName
        }

    }

}