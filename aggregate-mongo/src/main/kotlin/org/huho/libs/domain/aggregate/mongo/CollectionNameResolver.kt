package org.huho.libs.domain.aggregate.mongo

import org.huho.libs.domain.aggregate.Aggregate
import org.huho.libs.domain.aggregate.mongo.exceptions.MissingCollectionNameException
import org.huho.libs.domain.identity.AbstractIdentity

class CollectionNameResolver {
    fun <ID : AbstractIdentity, T : Aggregate<ID>> resolve(aggregateClass: Class<T>): String {
        val collectionNameAnnotation =
            aggregateClass.getAnnotation(CollectionName::class.java)
                ?: throw MissingCollectionNameException(aggregateClass)
        return collectionNameAnnotation.value
    }
}
