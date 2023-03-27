package com.sudoajay.namaz_alert.ui.background

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.sudoajay.namaz_alert.util.Helper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WorkMangerNotification(var context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {
    override fun doWork(): Result {

        runAgainAlarmManager(context)
        return Result.success()
    }

    private  fun runAgainAlarmManager(context: Context){
        val workMangerForTask = AlarmMangerForTask(context = context)
        CoroutineScope(Dispatchers.IO).launch {
            Helper.setIsAlarmMangerRunning(context, false)
            workMangerForTask.startWorker()
        }
    }
}