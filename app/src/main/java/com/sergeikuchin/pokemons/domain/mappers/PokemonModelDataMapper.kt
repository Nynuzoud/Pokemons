package com.sergeikuchin.pokemons.domain.mappers

import com.sergeikuchin.pokemons.db.entities.Pokemon
import com.sergeikuchin.pokemons.domain.models.PokemonModel

interface PokemonModelDataMapper {

    fun map(pokemon: Pokemon): PokemonModel
}