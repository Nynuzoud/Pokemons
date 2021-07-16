package com.sergeikuchin.pokemons.domain.usecases

import com.sergeikuchin.pokemons.db.entities.Pokemon
import com.sergeikuchin.pokemons.domain.ResponseError
import com.sergeikuchin.pokemons.domain.ResponseStatus
import com.sergeikuchin.pokemons.domain.errors.GeneralErrorHandler
import com.sergeikuchin.pokemons.domain.errors.GeneralErrorHandlerImpl
import com.sergeikuchin.pokemons.domain.mappers.PokemonModelDataMapper
import com.sergeikuchin.pokemons.domain.mappers.PokemonModelDataMapperImpl
import com.sergeikuchin.pokemons.repository.ResultStatus
import com.sergeikuchin.pokemons.repository.repositories.GetPokemonsRepoQuery
import com.sergeikuchin.pokemons.repository.repositories.GetPokemonsRepoResult
import com.sergeikuchin.pokemons.repository.repositories.GetPokemonsRepository
import com.sergeikuchin.pokemons.repository.repositories.RefreshPokemonsRepoResult
import io.mockk.*
import io.mockk.impl.annotations.MockK
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import org.junit.jupiter.api.*
import java.net.SocketTimeoutException

class GetPokemonsUseCaseTest {

    @MockK
    private lateinit var repository: GetPokemonsRepository

    private val pokemonMapper: PokemonModelDataMapper = spyk(PokemonModelDataMapperImpl())
    private val errorHandler: GeneralErrorHandler = spyk(GeneralErrorHandlerImpl())

    private lateinit var useCase: GetPokemonsUseCase

    private val pokemon1 = Pokemon(
        id = "1",
        name = "test1",
        imageUrl = "imageUrl1"
    )

    private val pokemon2 = Pokemon(
        id = "2",
        name = "test2",
        imageUrl = "imageUrl2"
    )

    private val pokemonMapperLazy = PokemonModelDataMapperImpl()

    private val pokemonModel1 = pokemonMapperLazy.map(pokemon1)
    private val pokemonModel2 = pokemonMapperLazy.map(pokemon2)

    @BeforeEach
    fun before() {
        MockKAnnotations.init(this)

        useCase = GetPokemonsUseCaseImpl(
            repository,
            pokemonMapper,
            errorHandler
        )
    }

    @Nested
    @DisplayName("Use Case success")
    inner class Nested0 {

        @BeforeEach
        fun before() {
            every { repository.request(GetPokemonsRepoQuery) } returns Observable.create { emitter ->
                emitter.onNext(
                    GetPokemonsRepoResult(
                        pokemons = listOf(pokemon1, pokemon2),
                        status = ResultStatus.Success
                    )
                )
            }
        }

        @AfterEach
        fun after() {
            verify(exactly = 1, verifyBlock = {
                pokemonMapper.map(pokemon1)
            })
            verify(exactly = 1, verifyBlock = {
                pokemonMapper.map(pokemon2)
            })

            verify(exactly = 1, verifyBlock = {
                repository.request(GetPokemonsRepoQuery)
            })

            confirmVerified(repository)
            confirmVerified(pokemonMapper)
        }

        @Test
        @DisplayName("Subscription success")
        fun test0() {

            useCase.getPokemons()
                .test()
                .assertValue(
                    GetPokemonsResponse(
                        pokemons = listOf(pokemonModel1, pokemonModel2),
                        status = ResponseStatus.Success
                    )
                )
                .assertValueCount(1)
                .assertNoErrors()
                .assertNotComplete()
                .dispose()
        }
    }

    @Nested
    @DisplayName("Use Case error")
    inner class Nested1 {

        private val expectedError = SocketTimeoutException()

        @BeforeEach
        fun before() {
            every { repository.request(GetPokemonsRepoQuery) } returns Observable.create { emitter ->
                emitter.onNext(
                    GetPokemonsRepoResult(
                        pokemons = emptyList(),
                        status = ResultStatus.Error(expectedError)
                    )
                )
            }
        }

        @AfterEach
        fun after() {
            verify(exactly = 1, verifyBlock = {
                errorHandler.handle(expectedError)
            })

            verify(exactly = 1, verifyBlock = {
                repository.request(GetPokemonsRepoQuery)
            })

            confirmVerified(repository)
            confirmVerified(errorHandler)
        }

        @Test
        @DisplayName("Subscription error")
        fun test0() {

            useCase.getPokemons()
                .test()
                .assertValue(
                    GetPokemonsResponse(
                        pokemons = listOf(),
                        status = ResponseStatus.Error(ResponseError.TimeoutException(expectedError))
                    )
                )
                .assertValueCount(1)
                .assertNoErrors()
                .assertNotComplete()
                .dispose()
        }
    }

    @Nested
    @DisplayName("Refresh")
    inner class Nested2 {

        @AfterEach
        fun after() {
            verify(exactly = 1, verifyBlock = {
                repository.refresh(GetPokemonsRepoQuery)
            })
            confirmVerified(repository)
        }

        @Test
        @DisplayName("Refresh success")
        fun test0() {

            every { repository.refresh(GetPokemonsRepoQuery) } returns Single.fromCallable {
                RefreshPokemonsRepoResult(
                    status = ResultStatus.Success
                )
            }

            useCase.refresh()
                .test()
                .assertValue(
                    RefreshPokemonsResponse(
                        status = ResponseStatus.Success
                    )
                )
                .assertValueCount(1)
                .assertNoErrors()
                .assertComplete()
                .dispose()

        }

        @Test
        @DisplayName("Refresh error")
        fun test1() {

            val expectedError = IllegalStateException()

            every { repository.refresh(GetPokemonsRepoQuery) } returns Single.fromCallable {
                RefreshPokemonsRepoResult(
                    status = ResultStatus.Error(expectedError)
                )
            }

            useCase.refresh()
                .test()
                .assertValue(
                    RefreshPokemonsResponse(
                        status = ResponseStatus.Error(ResponseError.UnknownError(expectedError))
                    )
                )
                .assertValueCount(1)
                .assertNoErrors()
                .assertComplete()
                .dispose()

            verify(exactly = 1, verifyBlock = {
                errorHandler.handle(expectedError)
            })

            confirmVerified(errorHandler)
        }
    }
}