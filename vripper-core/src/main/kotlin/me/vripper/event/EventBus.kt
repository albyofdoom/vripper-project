package me.vripper.event

import reactor.core.publisher.Flux
import reactor.core.publisher.Sinks
import reactor.core.scheduler.Scheduler
import reactor.core.scheduler.Schedulers
import java.util.concurrent.Executors

object EventBus {

    private val Scheduler: Scheduler = Schedulers.fromExecutor(Executors.newSingleThreadExecutor())
    private val _events = Sinks.many().multicast().onBackpressureBuffer<Any>()
    val events: Flux<Any> = _events.asFlux().publishOn(Scheduler)
    fun publishEvent(event: Any) {
        _events.tryEmitNext(event)
    }
}
