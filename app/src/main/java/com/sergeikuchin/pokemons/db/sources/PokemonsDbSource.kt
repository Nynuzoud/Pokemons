package com.sergeikuchin.pokemons.db.sources

import com.sergeikuchin.pokemons.db.*
import com.sergeikuchin.pokemons.db.entities.Pokemon

interface PokemonsDbSource :
    DbSource<GetPokemonsDbQuery, GetPokemonsDbResult>,
    DbMutableSource<PokemonsDbInsertion> {

    fun deleteAll()
}

object GetPokemonsDbQuery : DbQuery

data class GetPokemonsDbResult(
    val pokemons: List<Pokemon>
) : DbResult

data class PokemonsDbInsertion(
    val newPokemons: List<Pokemon>
) : DbInsertion