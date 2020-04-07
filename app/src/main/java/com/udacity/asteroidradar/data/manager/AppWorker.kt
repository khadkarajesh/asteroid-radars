package com.udacity.asteroidradar.data.manager

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.udacity.asteroidradar.BuildConfig
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.data.local.AsteroidsLocalRepository
import com.udacity.asteroidradar.data.remote.AsteroidApiService
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class AppWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params),
    KoinComponent {
    private val repository: AsteroidsLocalRepository by inject()
    override suspend fun doWork(): Result {
        return try {
            val today = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT).format(Date())
            repository.deletePastAsteroids(today)
            repository.refreshData()
            Result.success()
        } catch (ex: Exception) {
            Result.failure()
        }
    }

}