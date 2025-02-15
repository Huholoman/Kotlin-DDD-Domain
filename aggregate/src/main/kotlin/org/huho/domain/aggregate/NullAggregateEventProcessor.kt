package org.huho.domain.aggregate

class NullAggregateEventProcessor : AggregateEventProcessor {
    override suspend fun process(event: Any) {
        // do nothing
    }
}
