package org.huho.domain.aggregate

interface AggregateEventProcessor<Event : Any> {
    suspend fun process(event: Event)
}
