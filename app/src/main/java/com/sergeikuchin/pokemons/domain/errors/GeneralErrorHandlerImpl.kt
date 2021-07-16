package com.sergeikuchin.pokemons.domain.errors

import com.sergeikuchin.pokemons.domain.ResponseError
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Just a simplified solution
 * Probably, it would be better to add the ability to add custom error handlers for certain use cases.
 */

private const val NOT_FOUND_ERROR = 404
private const val SERVER_ERROR = 500

class GeneralErrorHandlerImpl : GeneralErrorHandler {

    override fun handle(e: Throwable): ResponseError {
        return when (e) {
            is SocketTimeoutException,
            is UnknownHostException -> ResponseError.TimeoutException(e)
            is HttpException -> mapHttpException(e)
            else -> ResponseError.UnknownError(e)
        }
    }

    private fun mapHttpException(e: HttpException): ResponseError = when (e.code()) {
        NOT_FOUND_ERROR -> ResponseError.HttpNotFoundError(e)
        SERVER_ERROR -> ResponseError.HttpServerError(e)
        else -> ResponseError.UnknownError(e)
    }

}