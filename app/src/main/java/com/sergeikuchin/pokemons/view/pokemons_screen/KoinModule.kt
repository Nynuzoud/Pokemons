package com.sergeikuchin.pokemons.view.pokemons_screen

import com.sergeikuchin.pokemons.view.pokemons_screen.fragments.pokemons_list.PokemonsListViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val pokemonsScreenModule = module {

    viewModel {
        PokemonsListViewModel(
            getPokemonsUseCase = get(),
            schedulersProvider = get()
        )
    }

}