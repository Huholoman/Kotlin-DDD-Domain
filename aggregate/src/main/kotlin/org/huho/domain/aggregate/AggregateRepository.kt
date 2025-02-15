package org.huho.domain.aggregate

import kotlinx.serialization.KSerializer
import org.huho.domain.identity.AbstractIdentity

interface AggregateRepository {
    suspend fun <ID : AbstractIdentity, T : Aggregate<ID>> insert(
        aggregate: T,
        serializer: KSerializer<T>,
    )

    suspend fun <ID : AbstractIdentity, T : Aggregate<ID>> save(
        aggregate: T,
        serializer: KSerializer<T>,
    )

    suspend fun <ID : AbstractIdentity, T : Aggregate<ID>> find(
        id: ID,
        aggregateClass: Class<T>,
        serializer: KSerializer<T>,
    ): T?

    suspend fun <ID : AbstractIdentity, T : Aggregate<ID>> get(
        id: ID,
        aggregateClass: Class<T>,
        serializer: KSerializer<T>,
    ): T

    suspend fun <ID : AbstractIdentity, T : Aggregate<ID>> exists(
        id: ID,
        aggregateClass: Class<T>,
        serializer: KSerializer<T>,
    ): Boolean
}
