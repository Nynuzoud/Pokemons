package com.sergeikuchin.pokemons.network.mappers

import com.sergeikuchin.pokemons.db.entities.Pokemon
import com.sergeikuchin.pokemons.network.dto.PokemonDTO

interface PokemonDataMapper {

    fun map(pokemonDTO: PokemonDTO): Pokemon
}