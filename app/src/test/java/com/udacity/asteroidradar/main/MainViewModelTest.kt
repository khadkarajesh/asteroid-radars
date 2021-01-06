package com.udacity.asteroidradar.main

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.udacity.asteroidradar.MainCoroutineRule
import com.udacity.asteroidradar.data.AsteroidFakeDataSource
import com.udacity.asteroidradar.data.dto.Result
import com.udacity.asteroidradar.data.remote.AsteroidApiService
import com.udacity.asteroidradar.data.remote.getClient
import com.udacity.asteroidradar.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

@RunWith(RobolectricTestRunner::class)
@Config(application = Application::class)
@ExperimentalCoroutinesApi
class MainViewModelTest {
    private lateinit var mainViewModel: MainViewModel
    private lateinit var fakeDataSource: AsteroidFakeDataSource

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var server: MockWebServer
    private lateinit var retrofit: Retrofit
    private lateinit var service: AsteroidApiService
    private lateinit var moshi: Moshi

    @Before
    fun setup() {
        fakeDataSource = AsteroidFakeDataSource()

        server = MockWebServer()

        moshi = Moshi
            .Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        retrofit = Retrofit
            .Builder()
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .baseUrl(server.url("/"))
            .build()

        service = retrofit.create(AsteroidApiService::class.java)

        mainViewModel = MainViewModel(
            ApplicationProvider.getApplicationContext(),
            fakeDataSource,
            service
        )
    }

    @Test
    fun getPictureOfDayShouldShowPicture() = runBlockingTest {
        server.enqueue(
            MockResponse().setBody(
                "{\"date\":\"2021-01-06\",\"explanation\":\"Why are these sand dunes on Mars striped? No one is sure. The featured image shows striped dunes in Kunowsky Crater on Mars, photographed recently with the Mars Reconnaissance Orbiter’s HiRISE Camera. Many Martian dunes are known to be covered unevenly with carbon dioxide (dry ice) frost, creating patterns of light and dark areas. Carbon dioxide doesn’t melt, but sublimates, turning directly into a gas. Carbon dioxide is also a greenhouse material even as a solid, so it can trap heat under the ice and sublimate from the bottom up, causing geyser-like eruptions. During Martian spring, these eruptions can cause a pattern of dark defrosting spots, where the darker sand is exposed. The featured image, though, was taken during Martian autumn, when the weather is getting colder – making these stripes particularly puzzling. One hypothesis is that they are caused by cracks in the ice that form from weaker eruptions or thermal stress as part of the day-night cycle, but research continues. Watching these dunes and others through more Martian seasons may give us more clues to solve this mystery.\",\"hdurl\":\"https://apod.nasa.gov/apod/image/2101/StripedDunes_HiRISE_1182.jpg\",\"media_type\":\"image\",\"service_version\":\"v1\",\"title\":\"Striped Sand Dunes on Mars\",\"url\":\"https://apod.nasa.gov/apod/image/2101/StripedDunes_HiRISE_1080.jpg\"}"
            ).setResponseCode(200)
        )

        mainViewModel.getPictureOfDay()

        assertThat(mainViewModel.pictureOfDay.getOrAwaitValue(), `is`(notNullValue()))
        assertThat(
            mainViewModel.pictureOfDay.getOrAwaitValue().url, `is`(notNullValue())
        )
    }


    @Test
    fun getAsteroidsWhenAsteroidUnavailableShouldShowSnackBar() = runBlockingTest {
        fakeDataSource.setReturnError(true)
        mainViewModel.getAsteroids()
        assertThat(
            mainViewModel.showSnackBar.getOrAwaitValue(), `is`("Asteroid not Found")
        )
    }

    @Test
    fun getAsteroidsShouldReturnAsteroids() = runBlockingTest {
        mainViewModel.getAsteroids()
        val data = fakeDataSource.getAsteroids() as Result.Success
        assertThat(mainViewModel.asteroids.getOrAwaitValue(), `is`(notNullValue()))
        assertThat(
            mainViewModel.asteroids.getOrAwaitValue(),
            IsEqual(fakeDataSource.dtoToPojo(data.data))
        )
    }

    @Test
    fun getAsteroidsShouldShowLoading() = runBlockingTest {
        mainCoroutineRule.pauseDispatcher()
        mainViewModel.getAsteroids()
        assertThat(mainViewModel.showLoading.getOrAwaitValue(), `is`(true))
        mainCoroutineRule.resumeDispatcher()
        assertThat(mainViewModel.showLoading.getOrAwaitValue(), `is`(false))
    }

    @After
    fun tearDown() {
        server.shutdown()
    }
}