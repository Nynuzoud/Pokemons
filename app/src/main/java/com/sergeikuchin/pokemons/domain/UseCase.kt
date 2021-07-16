package com.sergeikuchin.pokemons.domain

import retrofit2.HttpException

/**
 * This interface serves as kind of a mediator between Repository and ViewModel.
 * Here, we map data from Repository to data that the View can accept.
 */
abstract class UseCase<Q : UseCase.Query> {

    protected var query: Q? = null

    interface Query
    interface Response {
        val status: ResponseStatus
    }
    interface RefreshResponse {
        val status: ResponseStatus
    }
}

sealed class ResponseStatus {

    object Success : ResponseStatus()
    data class Error(val e: ResponseError) : ResponseStatus()
}

sealed class ResponseError(val errorMessage: String, cause: Throwable?) :
    Exception(errorMessage, cause) {

    data class HttpNotFoundError(override val cause: HttpException? = null) :
        ResponseError("HTTP Not Found Exception (404)", cause)

    data class HttpServerError(override val cause: HttpException? = null) :
        ResponseError("Server Error (500)", cause)

    data class TimeoutException(override val cause: Throwable? = null) :
        ResponseError("Socket Timeout Exception", cause)

    data class UnknownError(override val cause: Throwable?) :
        ResponseError("Unknown Error", cause)
}