package com.udacity.asteroidradar.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.asteroidradar.data.AsteroidDataSource
import com.udacity.asteroidradar.data.dto.AsteroidDTO
import com.udacity.asteroidradar.data.dto.Result
import com.udacity.asteroidradar.data.remote.Api
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@MediumTest
class AsteroidsLocalRepositoryTest {
    private lateinit var localDataSource: AsteroidDataSource
    private lateinit var database: AsteroidsDatabase

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AsteroidsDatabase::class.java
        ).allowMainThreadQueries()
            .build()

        localDataSource =
            AsteroidsLocalRepository(database.asteroidDao(), Api.retrofitService, Dispatchers.Main)
    }

    @Test
    fun saveAsteroidShouldSaveAsteroid() = runBlocking {
        val asteroidDto = AsteroidDTO(
            "1234",
            "2021-01-04",
            20.1,
            0.6824,
            true,
            20.36,
            20.20,
            2
        )
        localDataSource.saveAsteroid(asteroidDto)

        val result = localDataSource.getAsteroid(asteroidDto.id.toString())

        result as Result.Success

        assertThat(result.data.id, `is`(asteroidDto.id))
        assertThat(result.data.codename, `is`(asteroidDto.codename))
    }

    @After
    fun cleanUp() {
        database.close()
    }
}