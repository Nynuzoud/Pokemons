package com.sergeikuchin.pokemons.db

import io.reactivex.rxjava3.core.Observable

interface DbSource<in Q : DbQuery, R : DbResult> {

    fun subscribe(query: Q): Observable<R>
}

interface DbQuery
interface DbResult