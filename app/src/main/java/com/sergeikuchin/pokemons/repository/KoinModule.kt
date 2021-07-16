package com.sergeikuchin.pokemons.repository

import com.sergeikuchin.pokemons.network.mappers.PokemonDataMapper
import com.sergeikuchin.pokemons.network.mappers.PokemonDataMapperImpl
import com.sergeikuchin.pokemons.repository.repositories.GetPokemonsRepository
import com.sergeikuchin.pokemons.repository.repositories.GetPokemonsRepositoryImpl
import org.koin.dsl.module

val repositoryModule = module {

    factory<PokemonDataMapper> {
        PokemonDataMapperImpl()
    }

    factory<GetPokemonsRepository> {
        GetPokemonsRepositoryImpl(
            dbSource = get(),
            networkSource = get(),
            pokemonDataMapper = get(),
            schedulersProvider = get()
        )
    }
}