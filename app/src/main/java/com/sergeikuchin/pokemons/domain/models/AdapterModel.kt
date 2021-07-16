package com.sergeikuchin.pokemons.domain.models

interface AdapterModel {

    fun areItemsTheSame(other: AdapterModel): Boolean
    fun areContentsTheSame(other: AdapterModel): Boolean
}