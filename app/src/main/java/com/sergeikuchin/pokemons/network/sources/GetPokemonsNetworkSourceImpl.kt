package com.sergeikuchin.pokemons.network.sources

import com.sergeikuchin.pokemons.network.api.PokemonsAPI
import io.reactivex.rxjava3.core.Single

class GetPokemonsNetworkSourceImpl(
    private val api: PokemonsAPI
) : GetPokemonsNetworkSource {

    override fun retrieveData(query: GetPokemonsNetworkQuery): Single<GetPokemonsNetworkResult> =
        api.getPokemons()
}