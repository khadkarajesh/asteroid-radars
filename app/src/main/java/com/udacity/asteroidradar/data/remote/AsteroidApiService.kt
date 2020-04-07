package com.udacity.asteroidradar.data.remote

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.udacity.asteroidradar.Constants.BASE_URL
import kotlinx.coroutines.Deferred
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


private val moshi = Moshi
    .Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

fun getClient(): OkHttpClient {
    val logger = HttpLoggingInterceptor()
    logger.setLevel(HttpLoggingInterceptor.Level.BODY)
    logger.setLevel(HttpLoggingInterceptor.Level.BASIC)
    logger.setLevel(HttpLoggingInterceptor.Level.HEADERS)

    val okHttpClient = OkHttpClient()
    okHttpClient.newBuilder().addInterceptor(logger)
    return okHttpClient
}

private val retrofit = Retrofit
    .Builder()
    .client(getClient())
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .baseUrl(BASE_URL)
    .build()

interface AsteroidApiService {
    @GET("planetary/apod")
    fun getPictureOfDayAsync(@Query("api_key") apiKey: String): Deferred<PictureOfDay>

    @GET("neo/rest/v1/feed")
    fun getAsteroidsAsync(
        @Query("api_key") apiKey: String,
        @Query("start_date") startDate: String
    ): Deferred<ResponseBody>
}

object Api {
    val retrofitService: AsteroidApiService by lazy { retrofit.create(AsteroidApiService::class.java) }
}


