package com.udacity.asteroidradar.data.remote

import com.udacity.asteroidradar.data.AsteroidDataSource
import com.udacity.asteroidradar.data.dto.AsteroidDTO
import com.udacity.asteroidradar.data.dto.Result

class AsteroidRemoteRepository: AsteroidDataSource{
    override suspend fun getAsteroids(): Result<List<AsteroidDTO>> {
        TODO("Not yet implemented")
    }

    override suspend fun saveAsteroid(asteroid: AsteroidDTO) {
        TODO("Not yet implemented")
    }

    override suspend fun getAsteroid(id: String): Result<AsteroidDTO> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAllAsteroids() {
        TODO("Not yet implemented")
    }

}