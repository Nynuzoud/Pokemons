package com.sergeikuchin.pokemons.repository.repositories

import com.sergeikuchin.pokemons.db.sources.GetPokemonsDbQuery
import com.sergeikuchin.pokemons.db.sources.PokemonsDbInsertion
import com.sergeikuchin.pokemons.db.sources.PokemonsDbSource
import com.sergeikuchin.pokemons.network.mappers.PokemonDataMapper
import com.sergeikuchin.pokemons.network.sources.GetPokemonsNetworkQuery
import com.sergeikuchin.pokemons.network.sources.GetPokemonsNetworkSource
import com.sergeikuchin.pokemons.repository.ResultStatus
import com.sergeikuchin.pokemons.utils.SchedulersProvider
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

class GetPokemonsRepositoryImpl(
        private val dbSource: PokemonsDbSource,
        private val networkSource: GetPokemonsNetworkSource,
        private val pokemonDataMapper: PokemonDataMapper,
        private val schedulersProvider: SchedulersProvider
) : GetPokemonsRepository {

    override fun request(query: GetPokemonsRepoQuery): Observable<GetPokemonsRepoResult> =
            Observable.merge(
                    getPokemonsFromDb(GetPokemonsDbQuery),
                    getPokemonsFromNetwork(GetPokemonsNetworkQuery).toObservable()
            )
                    .subscribeOn(schedulersProvider.io())

    override fun refresh(query: GetPokemonsRepoQuery): Single<RefreshPokemonsRepoResult> =
            getPokemonsFromNetwork(GetPokemonsNetworkQuery)
                    .map(::mapGetToRefreshResult)
                    .subscribeOn(schedulersProvider.io())


    private fun mapGetToRefreshResult(getRepoResult: GetPokemonsRepoResult): RefreshPokemonsRepoResult =
            when (getRepoResult.status) {
                is ResultStatus.Error -> RefreshPokemonsRepoResult(ResultStatus.Error(getRepoResult.status.e))
                is ResultStatus.Success -> RefreshPokemonsRepoResult(ResultStatus.Success)
            }

    private fun getPokemonsFromDb(query: GetPokemonsDbQuery): Observable<GetPokemonsRepoResult> =
            dbSource.subscribe(query)
                    .filter { it.pokemons.isNotEmpty() }
                    .map { GetPokemonsRepoResult(it.pokemons, ResultStatus.Success) }
                    .onErrorReturn { GetPokemonsRepoResult(emptyList(), ResultStatus.Error(it)) }

    private fun getPokemonsFromNetwork(query: GetPokemonsNetworkQuery): Single<GetPokemonsRepoResult> =
            networkSource.retrieveData(query)
                    .map { result -> result.pokemons.map(pokemonDataMapper::map) }
                    .doOnSuccess {
                        dbSource.deleteAll()
                        dbSource.insertData(PokemonsDbInsertion(it))
                    }
                    .map { pokemons -> GetPokemonsRepoResult(pokemons, ResultStatus.Success) }
                    .onErrorReturn { GetPokemonsRepoResult(emptyList(), ResultStatus.Error(it)) }
}