package org.huho.domain.aggregate

import kotlinx.serialization.KSerializer
import org.huho.domain.identity.AbstractIdentity

interface AggregateRepository<Event : Any> {
    suspend fun <ID : AbstractIdentity, T : Aggregate<ID, Event>> insert(
        aggregate: T,
        serializer: KSerializer<T>,
    )

    suspend fun <ID : AbstractIdentity, T : Aggregate<ID, Event>> save(
        aggregate: T,
        serializer: KSerializer<T>,
    )

    suspend fun <ID : AbstractIdentity, T : Aggregate<ID, Event>> find(
        id: ID,
        aggregateClass: Class<T>,
        serializer: KSerializer<T>,
    ): T?

    suspend fun <ID : AbstractIdentity, T : Aggregate<ID, Event>> get(
        id: ID,
        aggregateClass: Class<T>,
        serializer: KSerializer<T>,
    ): T

    suspend fun <ID : AbstractIdentity, T : Aggregate<ID, Event>> exists(
        id: ID,
        aggregateClass: Class<T>,
        serializer: KSerializer<T>,
    ): Boolean
}
