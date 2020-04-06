package com.udacity.asteroidradar.data.remote

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Deferred
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

private const val BASE_URL = "https://api.nasa.gov/neo/rest/v1/"

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
    @GET("https://api.nasa.gov/planetary/apod?api_key=8NRiPaifF1kSclmlasVmXaifvqJrAUOAbG4EMFaA")
    fun getPictureOfDayAsync(): Deferred<PictureOfDay>
}

object Api {
    val retrofitService: AsteroidApiService by lazy { retrofit.create(AsteroidApiService::class.java) }
}


