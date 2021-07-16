package com.sergeikuchin.pokemons.db

import androidx.room.Room
import com.sergeikuchin.pokemons.db.sources.PokemonsDbSource
import com.sergeikuchin.pokemons.db.sources.PokemonsDbSourceImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dbModule = module {

    single {
        Room.databaseBuilder(
            androidContext(),
            Database::class.java,
            "pokemonsAppDatabase"
        ).build()
    }
}

val dbSourceModule = module {

    factory<PokemonsDbSource> {
        PokemonsDbSourceImpl(
            db = get()
        )
    }
}