package com.sergeikuchin.pokemons.domain.usecases

import com.sergeikuchin.pokemons.domain.ResponseStatus
import com.sergeikuchin.pokemons.domain.UseCase
import com.sergeikuchin.pokemons.domain.models.PokemonModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

interface GetPokemonsUseCase {

    fun getPokemons(): Observable<GetPokemonsResponse>
    fun refresh(): Single<RefreshPokemonsResponse>
}

object GetPokemonsQuery : UseCase.Query

data class GetPokemonsResponse(
    val pokemons: List<PokemonModel>,
    override val status: ResponseStatus
) : UseCase.Response

data class RefreshPokemonsResponse(
    override val status: ResponseStatus
) : UseCase.RefreshResponse