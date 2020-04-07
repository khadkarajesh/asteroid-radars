package com.udacity.asteroidradar.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.udacity.asteroidradar.data.dto.AsteroidDTO


@Dao
interface AsteroidDao {
    @Query("SELECT * FROM asteroids where close_approach_date >= :date ORDER BY date(close_approach_date)")
    suspend fun getAsteroidsTodayOnWards(date: String): List<AsteroidDTO>

    @Query("SELECT * FROM asteroids where close_approach_date < :date")
    suspend fun getPastAsteroids(date: String): List<AsteroidDTO>

    @Query("DELETE FROM asteroids where id=:id")
    suspend fun deleteById(id: Long)

    @Query("SELECT COUNT(*) FROM asteroids")
    suspend fun getCount(): Int

    @Query("SELECT * FROM asteroids where id = :asteroidId")
    suspend fun getAsteroidById(asteroidId: String): AsteroidDTO?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveAsteroid(asteroid: AsteroidDTO)

    @Query("DELETE FROM asteroids")
    suspend fun deleteAllAsteroids()
}