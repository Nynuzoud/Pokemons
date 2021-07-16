package com.sergeikuchin.pokemons.repository.repositories

import com.sergeikuchin.pokemons.db.entities.Pokemon
import com.sergeikuchin.pokemons.repository.*

interface GetPokemonsRepository : GetDataRepository<GetPokemonsRepoQuery, GetPokemonsRepoResult, RefreshPokemonsRepoResult>

object GetPokemonsRepoQuery : GetDataQuery

data class GetPokemonsRepoResult(
    val pokemons: List<Pokemon>,
    override val status: ResultStatus
) : GetDataResult

data class RefreshPokemonsRepoResult(
    override val status: ResultStatus
) : RefreshDataResult