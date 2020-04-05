package com.udacity.asteroidradar.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.udacity.asteroidradar.data.dto.AsteroidDTO

@Database(entities = [AsteroidDTO::class], version = 1, exportSchema = false)
abstract class AsteroidsDatabase : RoomDatabase() {
    abstract fun asteroidDao(): AsteroidDao
}