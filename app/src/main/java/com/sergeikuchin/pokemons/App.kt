package com.sergeikuchin.pokemons

import android.app.Application
import com.sergeikuchin.pokemons.servicelocator.Koin
import timber.log.Timber

@Suppress("UNUSED")
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        Koin.initKoin(this)

        Timber.plant(Timber.DebugTree())
    }
}