package com.udacity.asteroidradar.main

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.udacity.asteroidradar.base.BaseViewModel
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.data.AsteroidDataSource
import com.udacity.asteroidradar.data.dto.AsteroidDTO
import com.udacity.asteroidradar.data.dto.Result
import kotlinx.coroutines.launch

class MainViewModel(
    app: Application,
    private val dataSource: AsteroidDataSource
) : BaseViewModel(app) {

    private val asteroids = MutableLiveData<List<AsteroidDataItem>>()

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

                is Result.Error -> showSnackBar.value = result.message
            }
        }
        invalidateShowNoData()
    }

    private fun invalidateShowNoData() {
        showNoData.value = asteroids.value == null || asteroids.value!!.isEmpty()
    }
}