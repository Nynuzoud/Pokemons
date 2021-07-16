package com.sergeikuchin.pokemons.network

import com.sergeikuchin.pokemons.BuildConfig
import com.sergeikuchin.pokemons.network.api.PokemonsAPI
import com.sergeikuchin.pokemons.network.sources.GetPokemonsNetworkSource
import com.sergeikuchin.pokemons.network.sources.GetPokemonsNetworkSourceImpl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val networkModule = module {

    factory {
        OkHttpClient().newBuilder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addHttpLoggingInterceptor()
            .build()
    }

    single<Retrofit> {
        val okHttpClient: OkHttpClient = get()

        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .callFactory(okHttpClient)
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .build()
    }
}

val apiModule = module {

    single<PokemonsAPI> {
        get<Retrofit>().create(PokemonsAPI::class.java)
    }
}

val networkSourceModule = module {

    factory<GetPokemonsNetworkSource> {
        val api: PokemonsAPI = get()
        GetPokemonsNetworkSourceImpl(api)
    }
}

private fun OkHttpClient.Builder.addHttpLoggingInterceptor() = apply {
    if (!BuildConfig.DEBUG) {
        return@apply
    }
    val interceptor = HttpLoggingInterceptor()
    interceptor.level = HttpLoggingInterceptor.Level.BODY
    addInterceptor(interceptor)
}