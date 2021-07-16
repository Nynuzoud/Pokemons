package com.sergeikuchin.pokemons

import com.sergeikuchin.pokemons.utils.SchedulersProvider
import io.reactivex.rxjava3.core.Scheduler

class TestSchedulerProvider(
    private val scheduler: () -> Scheduler
) : SchedulersProvider {

    override fun mainThread(): Scheduler = scheduler()

    override fun io(): Scheduler = scheduler()

    override fun computation(): Scheduler = scheduler()
}