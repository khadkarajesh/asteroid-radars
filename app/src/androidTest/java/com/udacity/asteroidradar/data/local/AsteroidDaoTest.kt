package com.udacity.asteroidradar.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.udacity.asteroidradar.data.dto.AsteroidDTO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.notNullValue
import org.hamcrest.core.IsEqual
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class AsteroidDaoTest {
    private lateinit var database: AsteroidsDatabase

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun initDb() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AsteroidsDatabase::class.java
        ).build()
    }

    @Test
    fun saveAsteroidShouldPersistAsteroid() = runBlockingTest {
        val asteroid = AsteroidDTO(
            "1234",
            "2021-01-06",
            20.1,
            0.6824,
            true,
            20.36,
            20.20,
            1
        )

        database.asteroidDao().saveAsteroid(asteroid)

        val loadedAsteroid =
            database.asteroidDao().getAsteroidById(asteroidId = asteroid.id.toString())

        assertThat(loadedAsteroid, notNullValue())
        assertThat(asteroid.id, `is`(asteroid.id))
        assertThat(asteroid.codename, `is`(asteroid.codename))
    }

    @Test
    fun getCountShouldReturnAsteroidCount() = runBlockingTest {
        val asteroid = AsteroidDTO(
            "1234",
            "2021-01-06",
            20.1,
            0.6824,
            true,
            20.36,
            20.20,
            1
        )

        database.asteroidDao().saveAsteroid(asteroid)

        assertThat(database.asteroidDao().getCount(), IsEqual(1))
    }

    @Test
    fun deleteAllAsteroidsShouldTruncateAllRow() = runBlockingTest {
        val asteroid = AsteroidDTO(
            "1234",
            "2021-01-06",
            20.1,
            0.6824,
            true,
            20.36,
            20.20,
            1
        )
        database.asteroidDao().saveAsteroid(asteroid)

        database.asteroidDao().deleteAllAsteroids()

        assertThat(database.asteroidDao().getCount(), IsEqual(0))
    }

    @Test
    fun getAsteroidsTodayOnWardsShouldReturnDataTodayOnWards() = runBlockingTest {
        database.asteroidDao().saveAsteroid(
            AsteroidDTO(
                "1234",
                "2021-01-04",
                20.1,
                0.6824,
                true,
                20.36,
                20.20,
                2
            )
        )

        database.asteroidDao().saveAsteroid(
            AsteroidDTO(
                "1235",
                "2021-01-06",
                20.1,
                0.6824,
                true,
                20.36,
                20.20,
                1
            )
        )

        var asteriods = database.asteroidDao().getAsteroidsTodayOnWards("2021-01-05")
        assertThat(asteriods[0].codename, IsEqual("1235"))

    }

    @Test
    fun deleteByIdShouldDeleteAsteroid() = runBlockingTest {
        database.asteroidDao().saveAsteroid(
            AsteroidDTO(
                "1234",
                "2021-01-04",
                20.1,
                0.6824,
                true,
                20.36,
                20.20,
                2
            )
        )

        database.asteroidDao().deleteById(2)
        assertThat(database.asteroidDao().getCount(), IsEqual(0))
    }

    @After
    fun closeDb() = database.close()
}