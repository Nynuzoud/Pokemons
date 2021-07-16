package com.sergeikuchin.pokemons.domain.models

data class PokemonModel(
    val id: String,
    val name: String,
    val image: ImageModel? = null
) : AdapterModel {

    override fun areItemsTheSame(other: AdapterModel): Boolean =
        other is PokemonModel
                && other.id == id

    override fun areContentsTheSame(other: AdapterModel): Boolean =
        other is PokemonModel
                && other == this
}
