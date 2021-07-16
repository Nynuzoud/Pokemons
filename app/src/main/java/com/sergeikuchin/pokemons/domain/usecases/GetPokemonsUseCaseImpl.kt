package com.sergeikuchin.pokemons.domain.usecases

import com.sergeikuchin.pokemons.db.entities.Pokemon
import com.sergeikuchin.pokemons.domain.ResponseStatus
import com.sergeikuchin.pokemons.domain.UseCase
import com.sergeikuchin.pokemons.domain.errors.GeneralErrorHandler
import com.sergeikuchin.pokemons.domain.mappers.PokemonModelDataMapper
import com.sergeikuchin.pokemons.repository.ResultStatus
import com.sergeikuchin.pokemons.repository.repositories.GetPokemonsRepoQuery
import com.sergeikuchin.pokemons.repository.repositories.GetPokemonsRepository
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

class GetPokemonsUseCaseImpl(
    private val getPokemonsRepository: GetPokemonsRepository,
    private val pokemonModelDataMapper: PokemonModelDataMapper,
    private val errorHandler: GeneralErrorHandler
) : GetPokemonsUseCase, UseCase<GetPokemonsQuery>() {

    override fun getPokemons(): Observable<GetPokemonsResponse> = getPokemonsRepository
        .request(GetPokemonsRepoQuery)
        .map {
            when (it.status) {
                is ResultStatus.Error -> GetPokemonsResponse(
                    pokemons = emptyList(),
                    status = getErrorResponseStatus(it.status.e)
                )
                ResultStatus.Success -> handleSuccessResult(it.pokemons)
            }
        }

    override fun refresh(): Single<RefreshPokemonsResponse> = getPokemonsRepository
        .refresh(GetPokemonsRepoQuery)
        .map {
            when (it.status) {
                is ResultStatus.Error -> RefreshPokemonsResponse(getErrorResponseStatus(it.status.e))
                ResultStatus.Success -> RefreshPokemonsResponse(ResponseStatus.Success)
            }
        }

    private fun getErrorResponseStatus(e: Throwable) = ResponseStatus.Error(errorHandler.handle(e))

    private fun handleSuccessResult(pokemons: List<Pokemon>): GetPokemonsResponse =
        GetPokemonsResponse(
            pokemons = pokemons.map(pokemonModelDataMapper::map),
            status = ResponseStatus.Success
        )
}