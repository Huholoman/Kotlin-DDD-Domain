package org.huho.domain.aggregate

class NullAggregateEventProcessor<Event : Any> : AggregateEventProcessor<Event> {
    override suspend fun process(event: Event) {
        // do nothing
    }
}
