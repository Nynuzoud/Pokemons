package com.sergeikuchin.pokemons.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sergeikuchin.pokemons.db.dao.PokemonsDao
import com.sergeikuchin.pokemons.db.entities.Pokemon

@Database(entities = [Pokemon::class], version = 1)
abstract class Database : RoomDatabase() {
    abstract fun pokemonsDao(): PokemonsDao
}