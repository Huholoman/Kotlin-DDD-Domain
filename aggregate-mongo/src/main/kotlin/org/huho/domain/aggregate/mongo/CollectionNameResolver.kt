package org.huho.domain.aggregate.mongo

import org.huho.domain.aggregate.Aggregate
import org.huho.domain.aggregate.mongo.exceptions.MissingCollectionNameException
import org.huho.domain.identity.AbstractIdentity

class CollectionNameResolver {
    fun <ID : AbstractIdentity, T : Aggregate<ID, *>> resolve(aggregateClass: Class<T>): String {
        val collectionNameAnnotation =
            aggregateClass.getAnnotation(CollectionName::class.java)
                ?: throw MissingCollectionNameException(aggregateClass)
        return collectionNameAnnotation.value
    }
}
