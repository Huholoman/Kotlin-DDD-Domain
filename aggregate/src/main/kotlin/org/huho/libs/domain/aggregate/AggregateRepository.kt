package org.huho.libs.domain.aggregate

import org.huho.libs.domain.identity.AbstractIdentity

interface AggregateRepository {
    fun <ID : AbstractIdentity, T: Aggregate<ID>>insert(aggregate: T)
    fun <ID : AbstractIdentity, T: Aggregate<ID>>save(aggregate: T)
    fun <ID : AbstractIdentity, T : Aggregate<ID>> find(id: ID, aggregateClass: Class<T>): T?
    fun <ID : AbstractIdentity, T : Aggregate<ID>> get(id: ID, aggregateClass: Class<T>): T
    fun <ID : AbstractIdentity, T : Aggregate<ID>> exists(id: ID, aggregateClass: Class<T>): Boolean
}
