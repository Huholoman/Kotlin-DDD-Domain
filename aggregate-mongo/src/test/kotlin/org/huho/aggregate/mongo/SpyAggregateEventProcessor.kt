package org.huho.aggregate.mongo

import org.huho.domain.aggregate.AggregateEventProcessor

class SpyAggregateEventProcessor : AggregateEventProcessor {
    private val events = mutableListOf<Any>()

    fun pullEvents(): List<Any> = events.toList().also { events.clear() }

    override suspend fun process(event: Any) {
        events.add(event)
        // TODO: this should probably pass the event to the real handler
    }
}
