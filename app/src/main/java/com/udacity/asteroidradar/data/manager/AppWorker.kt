package com.udacity.asteroidradar.data.manager

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class AppWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    override fun doWork(): Result {
        return Result.success()
    }

}