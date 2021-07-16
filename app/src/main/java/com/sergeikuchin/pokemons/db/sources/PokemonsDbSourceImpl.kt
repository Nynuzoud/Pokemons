package com.sergeikuchin.pokemons.db.sources

import com.sergeikuchin.pokemons.db.Database
import io.reactivex.rxjava3.core.Observable

class PokemonsDbSourceImpl(
    private val db: Database
) : PokemonsDbSource {

    override fun subscribe(query: GetPokemonsDbQuery): Observable<GetPokemonsDbResult> =
        db.pokemonsDao().subscribe().map { GetPokemonsDbResult(it) }

    override fun insertData(newData: PokemonsDbInsertion) {
        db.pokemonsDao().insertAll(*newData.newPokemons.toTypedArray())
    }

    override fun deleteAll() {
        db.pokemonsDao().deleteAll()
    }
}