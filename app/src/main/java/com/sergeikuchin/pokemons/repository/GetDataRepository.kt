package com.sergeikuchin.pokemons.repository

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single


/**
 * Regular repository interface
 * If we need pagination in future, it is better to create a separate DataPageRepository.
 * Or it can be inherited from this [GetDataRepository]
 */
interface GetDataRepository<Q : GetDataQuery, R : GetDataResult, U : RefreshDataResult> {

    fun request(query: Q): Observable<R>
    fun refresh(query: Q): Single<U>

}

interface GetDataQuery
interface GetDataResult {
    val status: ResultStatus
}

interface RefreshDataResult {
    val status: ResultStatus
}

sealed class ResultStatus {
    object Success : ResultStatus()
    data class Error(val e: Throwable): ResultStatus()
}

