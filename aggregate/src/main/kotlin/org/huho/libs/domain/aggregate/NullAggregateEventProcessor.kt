package org.huho.libs.domain.aggregate

class NullAggregateEventProcessor : AggregateEventProcessor {
    override suspend fun process(event: Any) {
        // do nothing
    }
}
