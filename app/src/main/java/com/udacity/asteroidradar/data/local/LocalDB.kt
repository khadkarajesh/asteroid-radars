package com.udacity.asteroidradar.data.local

import android.content.Context
import androidx.room.Room

object LocalDB {
    fun createAsteroidDao(context: Context): AsteroidDao {
        return Room.databaseBuilder(
            context.applicationContext,
            AsteroidsDatabase::class.java, "asteroids.db"
        ).build().asteroidDao()
    }
}