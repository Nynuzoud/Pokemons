package com.sergeikuchin.pokemons.domain.errors

import com.sergeikuchin.pokemons.domain.ResponseError

interface GeneralErrorHandler {

    fun handle(e: Throwable): ResponseError
}