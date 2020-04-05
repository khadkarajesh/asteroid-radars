package com.udacity.asteroidradar.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.udacity.asteroidradar.data.dto.AsteroidDTO


@Dao
interface AsteroidDao {
    @Query("SELECT * FROM asteroids")
    suspend fun getAsteroids(): List<AsteroidDTO>

    @Query("SELECT * FROM asteroids where entry_id = :asteroidId")
    suspend fun getAsteroidById(asteroidId: String): AsteroidDTO?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveAsteroid(asteroid: AsteroidDTO)

    @Query("DELETE FROM asteroids")
    suspend fun deleteAllAsteroids()
}