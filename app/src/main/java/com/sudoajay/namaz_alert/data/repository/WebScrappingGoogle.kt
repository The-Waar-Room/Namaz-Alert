package com.sudoajay.namaz_alert.data.repository

import android.content.Context
import android.util.Log
import com.sudoajay.namaz_alert.data.db.DailyPrayerDB
import com.sudoajay.namaz_alert.data.db.DailyPrayerDatabase
import com.sudoajay.namaz_alert.ui.BaseFragment
import com.sudoajay.namaz_alert.ui.background.AlarmMangerForTask
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


    lateinit var workManger: AlarmMangerForTask

    suspend fun checkEvertTimeIfDataIsUpdated() {
        workManger = AlarmMangerForTask(context)

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

        cal.add(Calendar.DAY_OF_MONTH, 28)
        val nextDay = cal.get(Calendar.DAY_OF_MONTH)
        val nextMonth = SimpleDateFormat("MMM", Locale.ENGLISH).format(cal.time)


        var doc: Document? = null
        val result = kotlin.runCatching {
            doc = Jsoup.connect("https://www.google.com/search?q=Islamic+prayer+times").get()
        }
        val allInfo = doc?.getElementsByClass("bvYrdf")
        val getExactString =
            Regex(pattern = "$currentDay $currentMonth([\\s\\S]*?)$nextDay $nextMonth")
                .find(allInfo.toString())?.value
        Log.e("GetData", "$currentDay $currentMonth([\\s\\S]*?)$nextDay $nextMonth")

        Log.e("GetData", " data heree "+getExactString.toString())

        val getAllTime = Regex(pattern = "(\\d\\d):(\\d\\d)").findAll(getExactString.toString())
        val list = mutableListOf<DailyPrayerDB>()
        var count =0L
        cal = Calendar.getInstance()
        Log.e("GetData", " get size ")
        getAllTime.forEachIndexed { index, it ->
            Log.e("GetData", " Index  $index it ${it.value}  count $count  cal ${cal.get(Calendar.DAY_OF_MONTH)}" +
                    " ${SimpleDateFormat("MMM", Locale.ENGLISH).format(cal.time)}")
//            7 *6  removing extra
            if ( (index >=42) && (index%6 != 1) ) {
                val  date = formatDate.format(cal.time)
                list.add(DailyPrayerDB(count, date, getPrayerName(count), it.value))
                count++
                if(count.toInt() % 5 == 0)cal.add(Calendar.DAY_OF_MONTH, 1)

            }

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