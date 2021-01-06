package com.udacity.asteroidradar.main

import android.app.Application
import android.util.Log
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.work.*
import com.squareup.picasso.Picasso
import com.udacity.asteroidradar.BuildConfig
import com.udacity.asteroidradar.base.BaseViewModel
import com.udacity.asteroidradar.data.AsteroidDataSource
import com.udacity.asteroidradar.data.dto.AsteroidDTO
import com.udacity.asteroidradar.data.dto.Result
import com.udacity.asteroidradar.data.manager.AppWorker
import com.udacity.asteroidradar.data.remote.AsteroidApiService
import com.udacity.asteroidradar.data.remote.PictureOfDay
import com.udacity.asteroidradar.domain.AsteroidDataItem
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit


class MainViewModel(
    private val app: Application,
    private val dataSource: AsteroidDataSource,
    private val api: AsteroidApiService
) : BaseViewModel(app) {
    private val tag = MainViewModel::class.java.simpleName
    val asteroids = MutableLiveData<List<AsteroidDataItem>>()
    var pictureOfDay: MutableLiveData<PictureOfDay> = MutableLiveData()


    fun getPictureOfDay() {
        viewModelScope.launch {
            val pictureOfDayDeferred = api.getPictureOfDayAsync(BuildConfig.API_KEY)
            try {
                var result = pictureOfDayDeferred
                pictureOfDay.value = result
            } catch (e: Exception) {
                showErrorMessage.value = e.localizedMessage
            }
        }
    }


    fun getAsteroids() {
        showLoading.value = true

        viewModelScope.launch {
            val result = dataSource.getAsteroids()
            showLoading.postValue(false)
            when (result) {
                is Result.Success<*> -> {
                    val dataList = ArrayList<AsteroidDataItem>()
                    dataList.addAll((result.data as List<AsteroidDTO>).map { asteroid ->
                        AsteroidDataItem(
                            asteroid.id,
                            asteroid.codename,
                            asteroid.closeApproachDate,
                            asteroid.absoluteMagnitude,
                            asteroid.estimatedDiameter,
                            asteroid.relativeVelocity,
                            asteroid.distanceFromEarth,
                            asteroid.isPotentiallyHazardous
                        )
                    })
                    asteroids.value = dataList
                }

                is Result.Error ->
                    showSnackBar.value = result.message
            }
        }
        invalidateShowNoData()
    }

    private fun invalidateShowNoData() {
        showNoData.value = asteroids.value == null || asteroids.value!!.isEmpty()
    }

    fun startJob() {
        val constraints = Constraints.Builder()
            .setRequiresCharging(true)
            .setRequiredNetworkType(NetworkType.CONNECTED).build()
        val request =
            PeriodicWorkRequestBuilder<AppWorker>(1, TimeUnit.DAYS)
                .setConstraints(constraints)
                .setBackoffCriteria(
                    BackoffPolicy.LINEAR,
                    PeriodicWorkRequest.MIN_BACKOFF_MILLIS,
                    TimeUnit.MILLISECONDS
                )
                .build()
        WorkManager.getInstance()
            .enqueueUniquePeriodicWork("DataSync", ExistingPeriodicWorkPolicy.KEEP, request)
    }
}

@BindingAdapter("android:src")
fun loadImage(view: ImageView, imageUrl: String?) {
    if (!imageUrl.isNullOrEmpty()) {
        Picasso.get().load(imageUrl).into(view)
    }
}