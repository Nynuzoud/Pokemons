package com.sergeikuchin.pokemons.network.mappers

import com.sergeikuchin.pokemons.db.entities.Pokemon
import com.sergeikuchin.pokemons.network.dto.PokemonDTO

class PokemonDataMapperImpl : PokemonDataMapper {

    override fun map(pokemonDTO: PokemonDTO): Pokemon = with (pokemonDTO) {
        Pokemon(
            id = id,
            name = name,
            imageUrl = imagePath
        )
    }
}