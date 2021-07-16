package com.sergeikuchin.pokemons.repository.repositories

import com.sergeikuchin.pokemons.TestSchedulerProvider
import com.sergeikuchin.pokemons.db.sources.GetPokemonsDbQuery
import com.sergeikuchin.pokemons.db.sources.GetPokemonsDbResult
import com.sergeikuchin.pokemons.db.sources.PokemonsDbInsertion
import com.sergeikuchin.pokemons.db.sources.PokemonsDbSource
import com.sergeikuchin.pokemons.network.dto.PokemonDTO
import com.sergeikuchin.pokemons.network.mappers.PokemonDataMapper
import com.sergeikuchin.pokemons.network.mappers.PokemonDataMapperImpl
import com.sergeikuchin.pokemons.network.sources.GetPokemonsNetworkQuery
import com.sergeikuchin.pokemons.network.sources.GetPokemonsNetworkResult
import com.sergeikuchin.pokemons.network.sources.GetPokemonsNetworkSource
import com.sergeikuchin.pokemons.repository.ResultStatus
import io.mockk.*
import io.mockk.impl.annotations.MockK
import io.reactivex.rxjava3.core.Emitter
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.TestScheduler
import org.junit.jupiter.api.*

class GetPokemonsRepositoryTest {

    private var testScheduler = TestScheduler()

    private val schedulersProvider = TestSchedulerProvider { testScheduler }

    @MockK
    private lateinit var dbSource: PokemonsDbSource

    @MockK
    private lateinit var networkSource: GetPokemonsNetworkSource


    private val pokemonDataMapper: PokemonDataMapper = spyk(PokemonDataMapperImpl())

    private lateinit var repository: GetPokemonsRepository

    private val pokemonDTO1 = PokemonDTO(
        id = "1",
        name = "test1",
        imagePath = "imagePath1"
    )

    private val pokemonDTO2 = PokemonDTO(
        id = "2",
        name = "test2",
        imagePath = "imagePath2"
    )

    private val pokemonDTO3 = PokemonDTO(
        id = "3",
        name = "test3",
        imagePath = "imagePath3"
    )

    private val pokemonDTO4 = PokemonDTO(
        id = "4",
        name = "test4",
        imagePath = "imagePath4"
    )

    private val pokemonDataMapperLazy = PokemonDataMapperImpl()

    private val pokemon1 = pokemonDataMapperLazy.map(pokemonDTO1)
    private val pokemon2 = pokemonDataMapperLazy.map(pokemonDTO2)
    private val pokemon3 = pokemonDataMapperLazy.map(pokemonDTO3)
    private val pokemon4 = pokemonDataMapperLazy.map(pokemonDTO4)


    @BeforeEach
    fun before() {
        MockKAnnotations.init(this)

        repository = GetPokemonsRepositoryImpl(
            dbSource, networkSource, pokemonDataMapper, schedulersProvider
        )
    }

    @AfterEach
    fun after() {
        verify(exactly = 1, verifyBlock = {
            dbSource.subscribe(GetPokemonsDbQuery)
        })

        confirmVerified(dbSource)
        confirmVerified(networkSource)
        confirmVerified(pokemonDataMapper)
    }

    @Nested
    @DisplayName("Repository success")
    inner class Nested0 {

        @BeforeEach
        fun before() {
            justRun { dbSource.deleteAll() }
            justRun {
                dbSource.insertData(
                    PokemonsDbInsertion(
                        newPokemons = listOf(pokemon1, pokemon2)
                    )
                )
            }
        }

        @AfterEach
        fun after() {
            verify(exactly = 1, verifyBlock = {
                dbSource.deleteAll()
            })
            verify(exactly = 1, verifyBlock = {
                dbSource.insertData(
                    PokemonsDbInsertion(
                        newPokemons = listOf(pokemon1, pokemon2)
                    )
                )
            })

            verify(exactly = 1, verifyBlock = {
                networkSource.retrieveData(GetPokemonsNetworkQuery)
            })

            verify(exactly = 1, verifyBlock = {
                pokemonDataMapper.map(pokemonDTO1)
            })
            verify(exactly = 1, verifyBlock = {
                pokemonDataMapper.map(pokemonDTO2)
            })
        }

        @Test
        @DisplayName("Network returned success data, DB is empty")
        fun test0() {

            every { networkSource.retrieveData(GetPokemonsNetworkQuery) } returns Single.fromCallable {
                GetPokemonsNetworkResult(
                    pokemons = listOf(pokemonDTO1, pokemonDTO2)
                )
            }

            every { dbSource.subscribe(GetPokemonsDbQuery) } returns Observable.create { emitter ->
                emitter.onNext(
                    GetPokemonsDbResult(
                        pokemons = listOf()
                    )
                )
            }

            val testObserver = repository.request(GetPokemonsRepoQuery)
                .test()


            testScheduler.triggerActions()

            testObserver
                .assertValue(
                    GetPokemonsRepoResult(
                        pokemons = listOf(pokemon1, pokemon2),
                        status = ResultStatus.Success
                    ),
                )
                .assertValueCount(1)
                .assertNoErrors()
                .assertNotComplete()
                .dispose()
        }

        @Test
        @DisplayName("Network returned success data, DB is not empty")
        fun test1() {

            every { networkSource.retrieveData(GetPokemonsNetworkQuery) } returns Single.fromCallable {
                GetPokemonsNetworkResult(
                    pokemons = listOf(pokemonDTO1, pokemonDTO2)
                )
            }


            every { dbSource.subscribe(GetPokemonsDbQuery) } returns Observable.create { emitter ->
                emitter.onNext(
                    GetPokemonsDbResult(
                        pokemons = listOf(pokemon3, pokemon4)
                    )
                )
            }

            val testObserver = repository.request(GetPokemonsRepoQuery)
                .test()

            testScheduler.triggerActions()

            testObserver
                .assertValues(
                    GetPokemonsRepoResult(
                        pokemons = listOf(pokemon3, pokemon4),
                        status = ResultStatus.Success
                    ),
                    GetPokemonsRepoResult(
                        pokemons = listOf(pokemon1, pokemon2),
                        status = ResultStatus.Success
                    ),
                )
                .assertValueCount(2)
                .assertNoErrors()
                .assertNotComplete()
                .dispose()

            verify(exactly = 1, verifyBlock = {
                dbSource.insertData(
                    PokemonsDbInsertion(
                        newPokemons = listOf(pokemon1, pokemon2)
                    )
                )
            })

            verify(exactly = 0, verifyBlock = {
                pokemonDataMapper.map(pokemonDTO3)
            })
            verify(exactly = 0, verifyBlock = {
                pokemonDataMapper.map(pokemonDTO4)
            })
        }

    }

    @Nested
    @DisplayName("Repository error")
    inner class Nested1 {

        @Test
        @DisplayName("Network error, DB success")
        fun test0() {
            val expectedError = RuntimeException()

            every { networkSource.retrieveData(GetPokemonsNetworkQuery) } returns Single.error(
                expectedError
            )
            every { dbSource.subscribe(GetPokemonsDbQuery) } returns Observable.create { emitter ->
                emitter.onNext(
                    GetPokemonsDbResult(
                        pokemons = listOf(pokemon1, pokemon2)
                    )
                )
            }

            val testObserver = repository.request(GetPokemonsRepoQuery)
                .test()

            testScheduler.triggerActions()

            testObserver
                .assertValues(
                    GetPokemonsRepoResult(
                        pokemons = listOf(pokemon1, pokemon2),
                        status = ResultStatus.Success
                    ),
                    GetPokemonsRepoResult(
                        pokemons = listOf(),
                        status = ResultStatus.Error(expectedError)
                    ),
                )
                .assertValueCount(2)
                .assertNoErrors()
                .assertNotComplete()
                .dispose()

            verify(exactly = 0, verifyBlock = {
                dbSource.deleteAll()
            })
            verify(exactly = 0, verifyBlock = {
                dbSource.insertData(
                    PokemonsDbInsertion(
                        newPokemons = listOf(pokemon1, pokemon2)
                    )
                )
            })

            verify(exactly = 1, verifyBlock = {
                networkSource.retrieveData(GetPokemonsNetworkQuery)
            })
        }

        @Test
        @DisplayName("Network success, DB error")
        fun test1() {
            val expectedError = RuntimeException()

            justRun { dbSource.deleteAll() }
            justRun {
                dbSource.insertData(
                    PokemonsDbInsertion(
                        newPokemons = listOf(pokemon1, pokemon2)
                    )
                )
            }

            every { networkSource.retrieveData(GetPokemonsNetworkQuery) } returns Single.fromCallable {
                GetPokemonsNetworkResult(
                    pokemons = listOf(pokemonDTO1, pokemonDTO2)
                )
            }
            every { dbSource.subscribe(GetPokemonsDbQuery) } returns Observable.error(expectedError)

            val testObserver = repository.request(GetPokemonsRepoQuery)
                .test()

            testScheduler.triggerActions()

            testObserver
                .assertValues(
                    GetPokemonsRepoResult(
                        pokemons = listOf(),
                        status = ResultStatus.Error(expectedError)
                    ),
                    GetPokemonsRepoResult(
                        pokemons = listOf(pokemon1, pokemon2),
                        status = ResultStatus.Success
                    ),
                )
                .assertValueCount(2)
                .assertNoErrors()
                .assertComplete()
                .dispose()

            verify(exactly = 1, verifyBlock = {
                dbSource.deleteAll()
            })
            verify(exactly = 1, verifyBlock = {
                dbSource.insertData(
                    PokemonsDbInsertion(
                        newPokemons = listOf(pokemon1, pokemon2)
                    )
                )
            })

            verify(exactly = 1, verifyBlock = {
                networkSource.retrieveData(GetPokemonsNetworkQuery)
            })

            verify(exactly = 1, verifyBlock = {
                pokemonDataMapper.map(pokemonDTO1)
            })
            verify(exactly = 1, verifyBlock = {
                pokemonDataMapper.map(pokemonDTO2)
            })
        }

    }

    @Nested
    @DisplayName("Refresh")
    inner class Nester2 {

        @Test
        @DisplayName("Refresh is success")
        fun test0() {

            justRun { dbSource.deleteAll() }
            justRun {
                dbSource.insertData(
                    PokemonsDbInsertion(
                        newPokemons = listOf(pokemon1, pokemon2)
                    )
                )
            }

            justRun {
                dbSource.insertData(
                    PokemonsDbInsertion(
                        newPokemons = listOf(pokemon3, pokemon4)
                    )
                )
            }

            every { networkSource.retrieveData(GetPokemonsNetworkQuery) } returns Single.fromCallable {
                GetPokemonsNetworkResult(
                    pokemons = listOf(pokemonDTO1, pokemonDTO2)
                )
            } andThen Single.fromCallable {
                GetPokemonsNetworkResult(
                    pokemons = listOf(pokemonDTO3, pokemonDTO4)
                )
            }

            var externalEmitter: Emitter<GetPokemonsDbResult>? = null
            every { dbSource.subscribe(GetPokemonsDbQuery) } returns Observable.create { emitter ->
                externalEmitter = emitter
                emitter.onNext(
                    GetPokemonsDbResult(
                        pokemons = listOf(pokemon1, pokemon2)
                    )
                )
            }


            val testObserver = repository.request(GetPokemonsRepoQuery)
                .test()

            testScheduler.triggerActions()

            testObserver
                .assertValues(
                    GetPokemonsRepoResult(
                        pokemons = listOf(pokemon1, pokemon2),
                        status = ResultStatus.Success
                    ),
                    GetPokemonsRepoResult(
                        pokemons = listOf(pokemon1, pokemon2),
                        status = ResultStatus.Success
                    )
                )
                .assertValueCount(2)
                .assertNotComplete()
                .assertNoErrors()

            testScheduler.triggerActions()

            val refreshObserver = repository.refresh(GetPokemonsRepoQuery)
                .test()

            testScheduler.triggerActions()

            refreshObserver
                .assertValue(RefreshPokemonsRepoResult(ResultStatus.Success))
                .assertValueCount(1)
                .assertComplete()
                .dispose()

            externalEmitter?.onNext(
                GetPokemonsDbResult(
                    pokemons = listOf(pokemon3, pokemon4)
                )
            )

            testScheduler.triggerActions()

            testObserver
                .assertNotComplete()
                .assertValues(
                    GetPokemonsRepoResult(
                        pokemons = listOf(pokemon1, pokemon2),
                        status = ResultStatus.Success
                    ),
                    GetPokemonsRepoResult(
                        pokemons = listOf(pokemon1, pokemon2),
                        status = ResultStatus.Success
                    ),
                    GetPokemonsRepoResult(
                        pokemons = listOf(pokemon3, pokemon4),
                        status = ResultStatus.Success
                    )
                )
                .assertValueCount(3)
                .assertNoErrors()
                .assertNotComplete()
                .dispose()

            verify(exactly = 2, verifyBlock = {
                dbSource.deleteAll()
            })
            verify(exactly = 1, verifyBlock = {
                dbSource.insertData(
                    PokemonsDbInsertion(
                        newPokemons = listOf(pokemon1, pokemon2)
                    )
                )
            })

            verify(exactly = 1, verifyBlock = {
                dbSource.insertData(
                    PokemonsDbInsertion(
                        newPokemons = listOf(pokemon3, pokemon4)
                    )
                )
            })

            verify(exactly = 2, verifyBlock = {
                networkSource.retrieveData(GetPokemonsNetworkQuery)
            })

            verify(exactly = 1, verifyBlock = {
                pokemonDataMapper.map(pokemonDTO1)
            })
            verify(exactly = 1, verifyBlock = {
                pokemonDataMapper.map(pokemonDTO2)
            })
            verify(exactly = 1, verifyBlock = {
                pokemonDataMapper.map(pokemonDTO3)
            })
            verify(exactly = 1, verifyBlock = {
                pokemonDataMapper.map(pokemonDTO4)
            })
        }

        @Test
        @DisplayName("Refresh is not success")
        fun test1() {

            val expectedError = RuntimeException()

            every { networkSource.retrieveData(GetPokemonsNetworkQuery) } returns Single.error(
                expectedError
            )
            every { dbSource.subscribe(GetPokemonsDbQuery) } returns Observable.create { emitter ->
                emitter.onNext(
                    GetPokemonsDbResult(
                        pokemons = listOf(pokemon1, pokemon2)
                    )
                )
            }

            val testObserver = repository.request(GetPokemonsRepoQuery)
                .test()

            testScheduler.triggerActions()

            testObserver
                .assertValues(
                    GetPokemonsRepoResult(
                        pokemons = listOf(pokemon1, pokemon2),
                        status = ResultStatus.Success
                    ),
                    GetPokemonsRepoResult(
                        pokemons = emptyList(),
                        status = ResultStatus.Error(expectedError)
                    )
                )
                .assertValueCount(2)
                .assertNotComplete()
                .assertNoErrors()

            testScheduler.triggerActions()

            val refreshObserver = repository.refresh(GetPokemonsRepoQuery)
                .test()

            testScheduler.triggerActions()

            refreshObserver
                .assertValue(RefreshPokemonsRepoResult(ResultStatus.Error(expectedError)))
                .assertValueCount(1)
                .assertComplete()
                .dispose()

            testScheduler.triggerActions()

            testObserver
                .assertNotComplete()
                .assertValues(
                    GetPokemonsRepoResult(
                        pokemons = listOf(pokemon1, pokemon2),
                        status = ResultStatus.Success
                    ),
                    GetPokemonsRepoResult(
                        pokemons = emptyList(),
                        status = ResultStatus.Error(expectedError)
                    )
                )
                .assertValueCount(2)
                .assertNoErrors()
                .assertNotComplete()
                .dispose()

            verify(exactly = 0, verifyBlock = {
                dbSource.deleteAll()
            })
            verify(exactly = 0, verifyBlock = {
                dbSource.insertData(
                    PokemonsDbInsertion(
                        newPokemons = listOf(pokemon1, pokemon2)
                    )
                )
            })

            verify(exactly = 2, verifyBlock = {
                networkSource.retrieveData(GetPokemonsNetworkQuery)
            })
        }
    }
}