package com.udacity.asteroidradar.data

import com.udacity.asteroidradar.data.dto.AsteroidDTO
import com.udacity.asteroidradar.data.dto.Result
import com.udacity.asteroidradar.domain.AsteroidDataItem

class AsteroidFakeDataSource : AsteroidDataSource {
    private var shouldReturnError = false

    fun setReturnError(value: Boolean) {
        shouldReturnError = value
    }

    var astroids =
        listOf(
            AsteroidDTO(
                "1234",
                "2021-01-06",
                20.1,
                0.6824,
                true,
                20.36,
                20.20,
                1
            ),
            AsteroidDTO(
                "1234",
                "2021-01-06",
                20.1,
                0.6824,
                true,
                20.36,
                20.20,
                2
            )
        ).toMutableList()

    override suspend fun getAsteroids(): Result<List<AsteroidDTO>> {
        if (shouldReturnError) {
            return Result.Error(
                "Asteroid not Found"
            )
        }
        return Result.Success(astroids)
    }

    override suspend fun saveAsteroid(asteroid: AsteroidDTO) {
        astroids.add(asteroid)
    }

    override suspend fun getAsteroid(id: String): Result<AsteroidDTO> {
        if (shouldReturnError) {
            return Result.Error(
                "Asteroid not Found"
            )
        }
        return Result.Success(astroids.first { it.id == id.toLong() })
    }

    override suspend fun deleteAllAsteroids() {
        astroids.clear()
    }

    override suspend fun deletePastAsteroids(date: String) {
        astroids.clear()
    }

    fun dtoToPojo(asteroids: List<AsteroidDTO>): List<AsteroidDataItem> {
        val data = ArrayList<AsteroidDataItem>()
        data.addAll(asteroids.map { asteroid ->
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
        return data
    }
}