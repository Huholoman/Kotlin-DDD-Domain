package org.huho.libs.domain.aggregate

interface AggregateEventProcessor {
    suspend fun process(event: Any)
}
