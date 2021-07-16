package com.sergeikuchin.pokemons.network.sources

import com.sergeikuchin.pokemons.network.NetworkQuery
import com.sergeikuchin.pokemons.network.NetworkResult
import com.sergeikuchin.pokemons.network.NetworkSource
import com.sergeikuchin.pokemons.network.dto.PokemonDTO

/**
 * General interface for whole [GetPokemonsNetworkSource].
 * We can also add other interfaces here, for example, for posting data to a server.
 */
interface GetPokemonsNetworkSource : NetworkSource<GetPokemonsNetworkQuery, GetPokemonsNetworkResult>

object GetPokemonsNetworkQuery : NetworkQuery

data class GetPokemonsNetworkResult(
    val pokemons: List<PokemonDTO>
) : NetworkResult