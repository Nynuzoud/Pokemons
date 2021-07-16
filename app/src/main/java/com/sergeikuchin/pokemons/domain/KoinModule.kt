package com.sergeikuchin.pokemons.domain

import com.sergeikuchin.pokemons.domain.errors.GeneralErrorHandler
import com.sergeikuchin.pokemons.domain.errors.GeneralErrorHandlerImpl
import com.sergeikuchin.pokemons.domain.mappers.PokemonModelDataMapper
import com.sergeikuchin.pokemons.domain.mappers.PokemonModelDataMapperImpl
import com.sergeikuchin.pokemons.domain.usecases.GetPokemonsUseCase
import com.sergeikuchin.pokemons.domain.usecases.GetPokemonsUseCaseImpl
import org.koin.dsl.module

val useCaseModule = module {

    factory<PokemonModelDataMapper> { PokemonModelDataMapperImpl() }

    factory<GeneralErrorHandler> { GeneralErrorHandlerImpl() }

    factory<GetPokemonsUseCase> {
        GetPokemonsUseCaseImpl(
            getPokemonsRepository = get(),
            pokemonModelDataMapper = get(),
            errorHandler = get()
        )
    }
}