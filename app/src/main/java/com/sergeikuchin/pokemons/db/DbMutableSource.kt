package com.sergeikuchin.pokemons.db

interface DbMutableSource<in I : DbInsertion> {

    fun insertData(newData: I)
}

interface DbInsertion