package com.udacity.asteroidradar.data

import com.udacity.asteroidradar.data.dto.AsteroidDTO
import com.udacity.asteroidradar.data.dto.Result


interface AsteroidDataSource {
    suspend fun getAsteroids(): Result<List<AsteroidDTO>>
    suspend fun saveAsteroid(asteroid: AsteroidDTO)
    suspend fun getAsteroid(id: String): Result<AsteroidDTO>
    suspend fun deleteAllAsteroids()
    suspend fun deletePastAsteroids(date: String)
}