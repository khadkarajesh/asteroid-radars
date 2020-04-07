package com.udacity.asteroidradar.data.local

import android.util.Log
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.BuildConfig
import com.udacity.asteroidradar.Constants.API_QUERY_DATE_FORMAT
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.data.AsteroidDataSource
import com.udacity.asteroidradar.data.dto.AsteroidDTO
import com.udacity.asteroidradar.data.dto.Result
import com.udacity.asteroidradar.data.remote.AsteroidApiService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AsteroidsLocalRepository(
    private val asteroidDao: AsteroidDao,
    private val service: AsteroidApiService,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : AsteroidDataSource {
    private val empty = 0
    private val tag = AsteroidsLocalRepository::class.java.simpleName
    override suspend fun getAsteroids(): Result<List<AsteroidDTO>> = withContext(ioDispatcher) {
        return@withContext try {
            if (isFirstLunch()) toDomainModel(getRemoteAsteroids())
                .forEach {
                    asteroidDao.saveAsteroid(
                        it
                    )
                }
            Result.Success(asteroidDao.getAsteroids())
        } catch (ex: Exception) {
            Result.Error(ex.localizedMessage)
        }
    }

    private suspend fun isFirstLunch(): Boolean {
        return asteroidDao.getCount() == empty
    }

    private fun toDomainModel(asteroids: List<Asteroid>): List<AsteroidDTO> {
        val dtoList = ArrayList<AsteroidDTO>()
        dtoList.addAll(asteroids.map { asteroid ->
            AsteroidDTO(
                asteroid.codename,
                asteroid.closeApproachDate,
                asteroid.absoluteMagnitude,
                asteroid.estimatedDiameter,
                asteroid.isPotentiallyHazardous,
                asteroid.relativeVelocity,
                asteroid.distanceFromEarth,
                asteroid.id
            )
        })
        return dtoList
    }

    suspend fun getRecordCount(): Int = withContext(ioDispatcher) {
        return@withContext asteroidDao.getCount()
    }

    suspend fun refreshData() {
        toDomainModel(getRemoteAsteroids()).forEach { asteroidDao.saveAsteroid(it) }
    }

    private suspend fun getRemoteAsteroids(): List<Asteroid> {
        val today = SimpleDateFormat(API_QUERY_DATE_FORMAT).format(Date())
        val responseBody = service.getAsteroidsAsync(BuildConfig.API_KEY, today).await()
        val jsonObject = JSONObject(responseBody.string())
        return parseAsteroidsJsonResult(jsonObject)
    }

    override suspend fun saveAsteroid(asteroid: AsteroidDTO) = withContext(ioDispatcher) {
        asteroidDao.saveAsteroid(asteroid)
    }

    override suspend fun getAsteroid(id: String): Result<AsteroidDTO> = withContext(ioDispatcher) {
        try {
            val asteroid = asteroidDao.getAsteroidById(id)
            if (asteroid != null) {
                return@withContext Result.Success(asteroid)
            } else {
                return@withContext Result.Error("Asteroid.kt not found!")
            }
        } catch (exception: Exception) {
            Result.Error(exception.localizedMessage)
        }
    }

    override suspend fun deleteAllAsteroids() = withContext(ioDispatcher) {
        asteroidDao.deleteAllAsteroids()
    }

    override suspend fun deletePastAsteroids(date: String) {
        try {
            val asteroids = asteroidDao.getPastAsteroids(date)
            asteroids.forEach {
                asteroidDao.deleteById(it.id)
            }
        } catch (e: java.lang.Exception) {
            Log.d(tag, "database exception ${e.localizedMessage}")
        }
    }

}