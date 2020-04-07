package com.udacity.asteroidradar

import android.app.Application
import com.facebook.stetho.Stetho
import com.udacity.asteroidradar.data.AsteroidDataSource
import com.udacity.asteroidradar.data.local.AsteroidsLocalRepository
import com.udacity.asteroidradar.data.local.LocalDB
import com.udacity.asteroidradar.data.remote.Api
import com.udacity.asteroidradar.data.remote.AsteroidApiService
import com.udacity.asteroidradar.main.MainViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Stetho.initializeWithDefaults(this)
        val myModule = module {
            viewModel {
                MainViewModel(get(), get() as AsteroidDataSource, get() as AsteroidApiService)
            }


            single { AsteroidsLocalRepository(get(), get()) as AsteroidDataSource }
            single { AsteroidsLocalRepository(get(), get()) }
            single { LocalDB.createAsteroidDao(this@App) }
            single { Api.retrofitService }
        }

        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(listOf(myModule))
        }
    }
}