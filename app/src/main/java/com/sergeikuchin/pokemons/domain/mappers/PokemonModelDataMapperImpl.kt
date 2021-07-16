package com.sergeikuchin.pokemons.domain.mappers

import com.sergeikuchin.pokemons.db.entities.Pokemon
import com.sergeikuchin.pokemons.domain.models.ImageModel
import com.sergeikuchin.pokemons.domain.models.PokemonModel

class PokemonModelDataMapperImpl : PokemonModelDataMapper {

    override fun map(pokemon: Pokemon): PokemonModel = with(pokemon) {
        PokemonModel(
            id = id,
            name = name,
            image = ImageModel(
                imageUrl
            )
        )
    }
}