package com.sergeikuchin.pokemons.view.pokemons_screen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sergeikuchin.pokemons.domain.models.PokemonModel

class PokemonsViewModel :  ViewModel() {

    private val _pokemonModel = MutableLiveData<PokemonModel>()
    val pokemonModel: LiveData<PokemonModel> = _pokemonModel

    private val _transitionName = MutableLiveData<String>()
    val transitionName: LiveData<String> = _transitionName

    fun setPokemonModel(pokemonModel: PokemonModel) {
        _pokemonModel.value = pokemonModel
    }

    fun setTransitionName(name: String) {
        _transitionName.value = name
    }
}