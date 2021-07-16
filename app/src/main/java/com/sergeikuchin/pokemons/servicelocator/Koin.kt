package com.sergeikuchin.pokemons.servicelocator

import android.content.Context
import com.sergeikuchin.pokemons.db.dbModule
import com.sergeikuchin.pokemons.db.dbSourceModule
import com.sergeikuchin.pokemons.domain.useCaseModule
import com.sergeikuchin.pokemons.network.apiModule
import com.sergeikuchin.pokemons.network.networkModule
import com.sergeikuchin.pokemons.network.networkSourceModule
import com.sergeikuchin.pokemons.repository.repositoryModule
import com.sergeikuchin.pokemons.utils.SchedulersProvider
import com.sergeikuchin.pokemons.utils.SchedulersProviderImpl
import com.sergeikuchin.pokemons.view.pokemons_screen.pokemonsScreenModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.dsl.module

object Koin {

    private val appModule = module {
        single<SchedulersProvider> { SchedulersProviderImpl() }
    }

    fun initKoin(appContext: Context) {
        startKoin {
            androidContext(appContext)

            modules(listOf(
                appModule,
                dbSourceModule,
                dbModule,
                apiModule,
                networkSourceModule,
                networkModule,
                repositoryModule,
                useCaseModule,
                pokemonsScreenModule
            ))
        }
    }
}