package com.sergeikuchin.pokemons.network.api

import com.sergeikuchin.pokemons.network.sources.GetPokemonsNetworkResult
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET

interface PokemonsAPI {

    @GET("pokemon")
    fun getPokemons(): Single<GetPokemonsNetworkResult>

}