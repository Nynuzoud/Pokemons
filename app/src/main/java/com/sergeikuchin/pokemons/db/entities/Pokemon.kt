package com.sergeikuchin.pokemons.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pokemons")
data class Pokemon(
    @PrimaryKey val id: String,
    val name: String,
    val imageUrl: String? = null
)