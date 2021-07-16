package com.sergeikuchin.pokemons.network.dto

data class PokemonDTO(
    val id: String,
    val name: String,
    val imagePath: String? = null
)