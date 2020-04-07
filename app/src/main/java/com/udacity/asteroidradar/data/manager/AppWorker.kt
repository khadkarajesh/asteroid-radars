package com.udacity.asteroidradar.data.manager

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.udacity.asteroidradar.data.local.AsteroidsLocalRepository
import com.udacity.asteroidradar.utils.today
import org.koin.core.KoinComponent
import org.koin.core.inject

class AppWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params),
    KoinComponent {
    private val repository: AsteroidsLocalRepository by inject()
    private val tag = AppWorker::class.java.simpleName
    override suspend fun doWork(): Result {
        return try {
            repository.deletePastAsteroids(today())
            repository.refreshData()
            Result.success()
        } catch (ex: Exception) {
            Result.failure()
        }
    }

}