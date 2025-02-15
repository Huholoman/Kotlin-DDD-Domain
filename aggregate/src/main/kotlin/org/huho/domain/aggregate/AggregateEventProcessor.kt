package org.huho.domain.aggregate

interface AggregateEventProcessor {
    suspend fun process(event: Any)
}
