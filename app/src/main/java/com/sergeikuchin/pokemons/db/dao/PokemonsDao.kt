package com.sergeikuchin.pokemons.db.dao

import androidx.room.*
import com.sergeikuchin.pokemons.db.entities.Pokemon
import io.reactivex.rxjava3.core.Observable

@Dao
interface PokemonsDao {

    @Query("SELECT * FROM pokemons")
    fun subscribe(): Observable<List<Pokemon>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg pokemons: Pokemon)

    @Query("DELETE FROM pokemons")
    fun deleteAll()
}