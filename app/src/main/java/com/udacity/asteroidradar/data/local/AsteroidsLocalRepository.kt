package com.udacity.asteroidradar.data.local

import com.udacity.asteroidradar.data.AsteroidDataSource
import com.udacity.asteroidradar.data.dto.AsteroidDTO
import com.udacity.asteroidradar.data.dto.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception

class AsteroidsLocalRepository(
    private val asteroidDao: AsteroidDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : AsteroidDataSource {
    override suspend fun getAsteroids(): Result<List<AsteroidDTO>> = withContext(ioDispatcher) {
        return@withContext try {
            Result.Success(asteroidDao.getAsteroids())
        } catch (ex: Exception) {
            Result.Error(ex.localizedMessage)
        }
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
                return@withContext Result.Error("Asteroid not found!")
            }
        } catch (exception: Exception) {
            Result.Error(exception.localizedMessage)
        }
    }

    override suspend fun deleteAllAsteroids() = withContext(ioDispatcher) {
        asteroidDao.deleteAllAsteroids()
    }

}