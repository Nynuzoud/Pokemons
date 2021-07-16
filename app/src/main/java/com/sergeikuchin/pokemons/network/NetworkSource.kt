package com.sergeikuchin.pokemons.network

import io.reactivex.rxjava3.core.Single

interface NetworkSource<in Q : NetworkQuery, R : NetworkResult> {

    fun retrieveData(query: Q): Single<R>
}

interface NetworkQuery
interface NetworkResult