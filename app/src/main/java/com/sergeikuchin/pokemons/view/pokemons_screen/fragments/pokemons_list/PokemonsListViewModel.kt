package com.sergeikuchin.pokemons.view.pokemons_screen.fragments.pokemons_list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sergeikuchin.pokemons.domain.ResponseError
import com.sergeikuchin.pokemons.domain.ResponseStatus
import com.sergeikuchin.pokemons.domain.models.PokemonModel
import com.sergeikuchin.pokemons.domain.usecases.GetPokemonsResponse
import com.sergeikuchin.pokemons.domain.usecases.GetPokemonsUseCase
import com.sergeikuchin.pokemons.domain.usecases.RefreshPokemonsResponse
import com.sergeikuchin.pokemons.utils.SchedulersProvider
import com.sergeikuchin.pokemons.view.utils.Event
import io.reactivex.rxjava3.disposables.CompositeDisposable
import timber.log.Timber

class PokemonsListViewModel(
    private val getPokemonsUseCase: GetPokemonsUseCase,
    private val schedulersProvider: SchedulersProvider
) : ViewModel() {

    private val disposables = CompositeDisposable()

    private val pokemonsListLiveData = MutableLiveData<List<PokemonModel>>()
    private val swipeRefreshStateLiveData = MutableLiveData(false)
    private val errorLiveData = MutableLiveData<Event<ResponseError>>()

    init {
        getPokemons()
    }

    fun subscribeOnPokemons(): LiveData<List<PokemonModel>> = pokemonsListLiveData

    private fun getPokemons() {
        if (pokemonsListLiveData.value != null) return
        getPokemonsUseCase.getPokemons()
            .doOnSubscribe(disposables::add)
            .observeOn(schedulersProvider.mainThread())
            .doOnNext(::handlePokemons)
            .subscribe()
    }

    fun refresh() {
        swipeRefreshStateLiveData.value = true
        getPokemonsUseCase.refresh()
            .doOnSubscribe(disposables::add)
            .observeOn(schedulersProvider.mainThread())
            .doOnSuccess(::handleRefreshResponse)
            .subscribe()
    }

    fun subscribeOnErrors(): LiveData<Event<ResponseError>> = errorLiveData

    fun subscribeOnSwipeToRefreshState(): LiveData<Boolean> = swipeRefreshStateLiveData

    private fun handlePokemons(response: GetPokemonsResponse) {
        when (response.status) {
            is ResponseStatus.Error -> handleError(response.status.e)
            ResponseStatus.Success -> {
                pokemonsListLiveData.value = response.pokemons
                swipeRefreshStateLiveData.value = false
            }
        }
    }

    private fun handleRefreshResponse(response: RefreshPokemonsResponse) {
        if (response.status is ResponseStatus.Error) {
            Timber.e(response.status.e.errorMessage)
            handleError(response.status.e)
            swipeRefreshStateLiveData.value = false
        }
    }

    private fun handleError(error: ResponseError) {
        errorLiveData.value = Event(error)
    }

    override fun onCleared() {
        disposables.dispose()
        super.onCleared()
    }
}